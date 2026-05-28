package com.banco.transactions.infrastructure.acl;

// ╔════════════════════════════════════════════════════════════════════════╗
// ║  ANTI-CORRUPTION LAYER                                                 ║
// ║                                                                        ║
// ║  Este es el ÚNICO archivo de todo el módulo transactions/ que está     ║
// ║  autorizado a importar clases del módulo accounts/.                    ║
// ╚════════════════════════════════════════════════════════════════════════╝

// --- Imports propios del contexto de TRANSACTIONS ---
import com.banco.shared.domain.model.Money;
import com.banco.transactions.application.port.AccountFundsPort;
import com.banco.transactions.application.port.AccountSnapshot;

// --- Imports del contexto VECINO de ACCOUNTS (legítimos: viven en el ACL) ---
import com.banco.accounts.domain.exception.AccountNotFoundException;
import com.banco.accounts.domain.model.BankAccount;
import com.banco.accounts.domain.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementa AccountFundsPort (vocabulario de transacciones) y por dentro
 * habla con BankAccount + AccountRepository (vocabulario de cuentas).
 *
 * Si mañana accounts cambia su entidad BankAccount o renombra algo,
 * SOLO este archivo necesita ajustes. El use case de transferencias
 * no se entera.
 */
@RequiredArgsConstructor
public class AccountsContextAdapter implements AccountFundsPort {

    private final AccountRepository accountRepository;

    @Override
    public AccountSnapshot lookup(String accountNumber) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + accountNumber));
        return toSnapshot(account);
    }

    @Override
    public void moveFunds(String fromAccountNumber, String toAccountNumber, Money amount) {
        BankAccount fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "From account not found: " + fromAccountNumber));

        BankAccount toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "To account not found: " + toAccountNumber));

        // Validaciones cross-aggregate (antes en la clase Transfer).
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (!fromAccount.getBalance().getCurrency().equals(toAccount.getBalance().getCurrency())) {
            throw new IllegalArgumentException(
                    "Accounts have different currencies: " +
                            fromAccount.getBalance().getCurrency() + " vs " +
                            toAccount.getBalance().getCurrency());
        }

        fromAccount.debit(amount);
        toAccount.credit(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    private static AccountSnapshot toSnapshot(BankAccount account) {
        return new AccountSnapshot(
                account.getAccountNumber(),
                account.getHolderName(),
                account.getBalance()
        );
    }
}