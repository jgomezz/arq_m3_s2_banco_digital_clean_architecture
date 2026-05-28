# Anti-Corruption Layer (ACL)

This document summarizes how the **Anti-Corruption Layer** is implemented between the `transactions` and `accounts` subdomains in this project.

## Why an ACL?

`transactions` needs balances and the ability to move funds, both of which live in `accounts`. Without an ACL, the transactions code would import `BankAccount`, `AccountRepository`, and `AccountNotFoundException` directly — coupling the two subdomains so that any change in `accounts` ripples into `transactions`.

The ACL prevents that coupling: `transactions` expresses what it needs **in its own vocabulary** (a port + a snapshot), and a single adapter translates between that vocabulary and the real `accounts` types.

## The pieces

| Role                 | Type                                                                                         | Location                                              |
| -------------------- | -------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| Port (interface)     | `AccountFundsPort`                                                                           | `transactions/application/port/`                      |
| Snapshot (read DTO)  | `AccountSnapshot` (`record`)                                                                 | `transactions/application/port/`                      |
| Adapter (the ACL)    | `AccountsContextAdapter implements AccountFundsPort`                                         | `transactions/infrastructure/acl/`                    |
| Consumer (use case)  | `TransferMoneyUseCase`                                                                       | `transactions/application/usecase/`                   |
| Wiring               | `accountFundsPort(...)` `@Bean`                                                              | `shared/infrastructure/config/BeanConfiguration.java` |

```
┌─────────────────────────── transactions ───────────────────────────┐
│                                                                    │
│  TransferMoneyUseCase ──► AccountFundsPort ──► AccountSnapshot     │
│                                  ▲                                 │
│                                  │ implements                      │
│                                  │                                 │
│                  AccountsContextAdapter  (ACL, only file allowed   │
│                                  │       to import from accounts/) │
└──────────────────────────────────┼─────────────────────────────────┘
                                   │ uses
                                   ▼
┌─────────────────────────── accounts ───────────────────────────────┐
│  AccountRepository · BankAccount · AccountNotFoundException        │
└────────────────────────────────────────────────────────────────────┘
```

## The load-bearing rule

Inside the `transactions` subdomain, **only** files under `transactions/infrastructure/acl/` are allowed to import from `com.banco.accounts.*`. An import of `com.banco.accounts.*` anywhere else in `transactions/` is a design violation, not a stylistic one.

The reverse is stricter: `accounts` must **not** import anything from `com.banco.transactions.*` at all.

## What the port exposes

`AccountFundsPort` is written in the *transactions* vocabulary, not the *accounts* vocabulary:

```java
public interface AccountFundsPort {
    AccountSnapshot lookup(String accountNumber);
    void moveFunds(String fromAccountNumber, String toAccountNumber, Money amount);
}
```

`AccountSnapshot` is an immutable `record` containing only the fields transactions actually needs (`accountNumber`, `holderName`, `balance`). It is **not** `BankAccount` — the aggregate never crosses the boundary.

## What the adapter does

`AccountsContextAdapter` is the single bridge. It:

1. Resolves account numbers to `BankAccount` aggregates via `AccountRepository`.
2. Translates "account not found" from `AccountNotFoundException` (an *accounts* exception) into the same exception type re-thrown at the boundary — the use case never catches an accounts-specific type directly.
3. Maps `BankAccount` → `AccountSnapshot` before returning it to the use case.
4. Hosts the **cross-aggregate** validations that don't belong on a single aggregate:
   - reject transfer to the same account,
   - reject transfer between accounts with different currencies.
5. Calls `BankAccount.debit` / `credit` and persists both aggregates.

## Sequence diagram — `POST /api/transactions/transfer`

The diagram below traces a full transfer request and makes the ACL boundary explicit.
Everything inside the dashed box is the `transactions` subdomain; everything outside it is `accounts`. The only crossings occur inside `AccountsContextAdapter`.

```plantuml
@startuml ACL Pattern - Transfer Money
title Anti-Corruption Layer — POST /api/transactions/transfer

skinparam sequenceMessageAlign center
skinparam ParticipantPadding 8
skinparam BoxPadding 10

actor Client

box "transactions subdomain" #F5F5F5
    boundary "TransactionController"          as Ctrl
    control  "TransferMoneyUseCase"           as UC
    interface "AccountFundsPort\n<<port>>"    as Port
    interface "NotificationPort\n<<port>>"    as Notif
    participant "AccountsContextAdapter\n<<ACL adapter>>" as ACL
end box

box "accounts subdomain" #EBF5FF
    entity "AccountRepository\n<<repository>>" as Repo
    entity "BankAccount\n<<aggregate root>>"   as Acc
end box

Client -> Ctrl : POST /api/transactions/transfer\n(TransferRequest)
activate Ctrl
Ctrl -> UC : execute(TransferCommand)
activate UC

== 1) Pre-transfer lookup (transactions vocabulary) ==

UC   -> Port : lookup(fromAccountNumber)
Port -> ACL  : lookup(from)
activate ACL
ACL  -> Repo : findByAccountNumber(from)
Repo --> ACL : Optional<BankAccount>
ACL  --> UC  : AccountSnapshot(from)
deactivate ACL

UC   -> Port : lookup(toAccountNumber)
Port -> ACL  : lookup(to)
activate ACL
ACL  -> Repo : findByAccountNumber(to)
Repo --> ACL : Optional<BankAccount>
ACL  --> UC  : AccountSnapshot(to)
deactivate ACL

note over UC : amount := Money.of(...)

== 2) Move funds (ACL hosts cross-aggregate rules) ==

UC   -> Port : moveFunds(from, to, Money)
Port -> ACL  : moveFunds(from, to, Money)
activate ACL
ACL  -> Repo : findByAccountNumber(from)
Repo --> ACL : BankAccount(from)
ACL  -> Repo : findByAccountNumber(to)
Repo --> ACL : BankAccount(to)

note right of ACL
  Cross-aggregate validations:
  • reject same-account transfer
  • reject currency mismatch
end note

ACL  -> Acc  : from.debit(Money)
ACL  -> Acc  : to.credit(Money)
ACL  -> Repo : save(from)
ACL  -> Repo : save(to)
ACL  --> UC  : void
deactivate ACL

== 3) Post-transfer lookup + notify ==

UC   -> Port : lookup(from)
Port -> ACL  : lookup(from)
ACL  --> UC  : AccountSnapshot(from, newBalance)

UC   -> Port : lookup(to)
Port -> ACL  : lookup(to)
ACL  --> UC  : AccountSnapshot(to, newBalance)

UC   -> Notif : notifyTransferSent(...)
UC   -> Notif : notifyTransferReceived(...)

UC   --> Ctrl
deactivate UC
Ctrl --> Client : 200 OK "Transfer completed successfully"
deactivate Ctrl

@enduml
```

> **Rendering:** the snippet above is **PlantUML** (a UML 2.x dialect).
> In IntelliJ install the *PlantUML Integration* plugin, or paste into <https://www.plantuml.com/plantuml/uml/> to view.

Key things the diagram makes visible:

- `TransferMoneyUseCase` talks **only** to `AccountFundsPort` and `NotificationPort`. It never sees `BankAccount`, `AccountRepository`, or any `accounts` exception.
- The arrows that *actually* cross from `transactions` into `accounts` all originate inside `AccountsContextAdapter` — that is the ACL.
- Cross-aggregate validations (same-account, same-currency) are placed in `moveFunds`, not in the use case and not on `BankAccount`.
- The post-transfer `lookup` calls (step 3) are how the use case obtains the updated balances for notification without ever touching the aggregate directly.

## How the use case stays clean

`TransferMoneyUseCase` depends on `AccountFundsPort` and `NotificationPort` only — it knows nothing about `BankAccount`, `AccountRepository`, JPA, or H2. The transfer flow is:

```
lookup(from)  →  lookup(to)  →  moveFunds(from, to, amount)
              →  lookup(from)  →  lookup(to)  →  notify
```

The re-`lookup` after `moveFunds` is how the use case obtains post-transfer balances without ever touching the aggregate.

## Wiring

Use cases are not auto-discovered with `@Service`; they are registered manually so the application layer stays framework-free. In `BeanConfiguration`:

```java
@Bean
public AccountFundsPort accountFundsPort(AccountRepository accountRepository) {
    return new AccountsContextAdapter(accountRepository);
}

@Bean
public TransferMoneyUseCase transferMoneyUseCase(
        AccountFundsPort accountFundsPort,
        NotificationPort notificationPort) {
    return new TransferMoneyUseCase(accountFundsPort, notificationPort);
}
```

Note that `transferMoneyUseCase` receives the **port**, not `AccountRepository` — Spring would happily inject either, but injecting the port is what enforces the architectural rule at the wiring level.

## Testing across the boundary

Because the use case depends on an interface, tests substitute the port with a hand-rolled in-memory fake instead of Mockito (see `TransferMoneyUseCaseTest`). The test exercises the use case's orchestration logic without booting Spring, JPA, or the ACL adapter itself.

## How to extend the ACL

When a new transactions feature needs more data or behavior from accounts, **do not** add an import — instead:

1. Add the method to `AccountFundsPort` in the transactions vocabulary.
2. Add any new fields to `AccountSnapshot` (or introduce a new snapshot record) — still in transactions terms.
3. Implement the new method in `AccountsContextAdapter`, doing the translation there.
4. Leave the use case talking to the port.

If accounts later renames `BankAccount`, splits the aggregate, or swaps its persistence strategy, **only `AccountsContextAdapter` should need to change**. That is the whole point of the ACL.
