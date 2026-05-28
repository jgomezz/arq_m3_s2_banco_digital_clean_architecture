# Project Overview

A Spring Boot REST API built with Clean Architecture + DDD. The codebase is split into three **subdomains**:

- `accounts` — core subdomain, owns the `BankAccount` aggregate and balance state.
- `transactions` — core subdomain, orchestrates transfers between accounts via an Anti-Corruption Layer.
- `shared` — supporting subdomain with cross-cutting primitives (`Money` VO, base `DomainException`, root `BeanConfiguration`).

## Stack
- Java 21
- Spring Boot 3.x
- Maven
- MariaDB via Spring Data JPA
- JUnit 5 + Mockito for tests
- MapStruct for DTO mapping

## Conventions

### Subdomains
- Code is organized by **subdomain**, not by technical layer. The three subdomains are `com.banco.accounts`, `com.banco.transactions`, and `com.banco.shared`.
- `accounts` and `transactions` are **core subdomains**; each owns its own aggregates, ports, and adapters. `shared` is a **supporting subdomain** for cross-cutting primitives only — do not put `accounts`- or `transactions`-specific logic there.
- Every subdomain follows the same internal layout: `domain/`, `application/`, and `infrastructure/`. There are no cross-subdomain `service/` or `controller/` packages at the root.
- A subdomain owns its REST surface: `accounts` exposes `/api/accounts`, `transactions` exposes `/api/transactions`. `shared` exposes nothing over HTTP.

### Anti-Corruption Layer between subdomains (load-bearing rule)
> Full walkthrough (port, adapter, snapshot, sequence diagram, extension guide): see [ACL.md](ACL.md).

- The `transactions` subdomain must **not** import anything from `com.banco.accounts.*` except inside `transactions/infrastructure/acl/`. `accounts` must **not** import anything from `com.banco.transactions.*` at all.
- The transactions use case talks to the accounts subdomain only through `AccountFundsPort` and the immutable `AccountSnapshot` record — never through `BankAccount` or `AccountRepository`.
- Cross-aggregate validations (same-account guard, currency match) live in the ACL adapter's `moveFunds`, not in the use case.
- When a use case in `transactions` needs new account data, extend `AccountFundsPort` + `AccountSnapshot`, not the import list.
- Both subdomains may freely depend on `shared` (it is intentionally upstream of both).

### Layer rules inside each subdomain
- **domain/** — aggregates, value objects, domain exceptions, repository **interfaces**. No Spring, no JPA, no Lombok `@Data` (use `@Getter` + explicit constructors to preserve invariants). Aggregates enforce their own invariants via constructor + behavior methods (`debit`, `credit`); no public setters except where strictly required for JPA reconstruction.
- **application/** — use cases (one class per use case, suffix `UseCase`), command DTOs (suffix `Command`), and the ports this subdomain **consumes** (`port/`). Keep framework-free: only Lombok `@RequiredArgsConstructor` is allowed; **no** `@Service`, `@Component`, or `@Transactional` annotations here.
- **infrastructure/** — Spring/JPA/web adapters. Subpackages: `web/controller`, `web/dto` (suffix `Request` / `Response`), `persistence/{entity,mapper,repository,adapter}`, `notification/`, and `acl/` (transactions only).

### Wiring
- Use cases are registered manually as `@Bean`s in `shared/infrastructure/config/BeanConfiguration.java` — **do not** annotate use cases with `@Service`.
- Adapters use `@Component`; MapStruct mappers use `@Mapper(componentModel = "spring")`.
- Constructor injection only (Lombok `@RequiredArgsConstructor` on `final` fields). No `@Autowired` on fields or setters.

### Persistence
- Domain aggregates and JPA entities are **separate types** (`BankAccount` vs `AccountEntity`); never annotate the domain model with `@Entity`.
- MapStruct generates `toEntity()`; `toDomain()` is a hand-written `default` method that rebuilds the aggregate via its full constructor, not via setters.
- Repository interfaces live in `domain/repository/`; the JPA-backed adapter lives in `infrastructure/persistence/adapter/`.

### Web
- REST controllers expose use cases under `/api/<subdomain>` (`/api/accounts`, `/api/transactions`). No global version prefix is used in this project.
- Controllers translate `Request` DTOs → `Command` → use case, and aggregate → `Response` DTO on the way back. Domain types are not returned directly.
- Use `ResponseEntity<T>` for explicit status codes.

### Testing
- Tests mirror the source tree under `src/test/java/com/banco/...`.
- For use-case tests, hand-roll in-memory fakes of the ports (see `TransferMoneyUseCaseTest`) — **do not** use Mockito for ports. Mockito is fine for infrastructure-level tests if needed.
- Domain model tests (`MoneyTest`, `BankAccountTest`) cover invariants directly with no Spring context.

## Build and Test
- Build: `./mvnw clean install`
- Run tests: `./mvnw test`
- Run app: `./mvnw spring-boot:run`

## What I Want to Improve
[Fill this in as you go — see Step 4]