#  Banco Digital - Clean Architecture + DDD 

Sistema bancario simple que implementa **Domain-Driven Design (DDD)** y **Clean Architecture** con Spring Boot

## Funcionalidades

- Crear cuentas bancarias
- Transferir dinero entre cuentas
- Consultar saldo
- Validación de saldo suficiente
- Notificaciones por consola

## Estructura del Proyecto

Organizada por **subdominios** (`accounts`, `transactions`, `shared`). Cada subdominio sigue las tres capas de Clean Architecture (`domain/`, `application/`, `infrastructure/`). El subdominio `transactions` incluye además un paquete `infrastructure/acl/` que aloja el **Anti-Corruption Layer** hacia `accounts` (ver [ACL.md](ACL.md)).

```
├── pom.xml
├── README.md
├── CLAUDE.md
├── ACL.md
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── banco
    │   │           ├── BancoDigitalApplication.java
    │   │           │
    │   │           ├── accounts                                 ← subdominio CORE
    │   │           │   ├── application
    │   │           │   │   ├── dto
    │   │           │   │   │   └── CreateAccountCommand.java
    │   │           │   │   └── usecase
    │   │           │   │       ├── CreateAccountUseCase.java
    │   │           │   │       └── GetBalanceUseCase.java
    │   │           │   ├── domain
    │   │           │   │   ├── exception
    │   │           │   │   │   ├── AccountNotFoundException.java
    │   │           │   │   │   └── InsufficientFundsException.java
    │   │           │   │   ├── model
    │   │           │   │   │   ├── AccountStatus.java
    │   │           │   │   │   └── BankAccount.java            ← Aggregate Root
    │   │           │   │   └── repository
    │   │           │   │       └── AccountRepository.java      ← puerto (interface)
    │   │           │   └── infrastructure
    │   │           │       ├── persistence
    │   │           │       │   ├── adapter
    │   │           │       │   │   └── AccountRepositoryAdapter.java
    │   │           │       │   ├── entity
    │   │           │       │   │   └── AccountEntity.java      ← JPA, separado del dominio
    │   │           │       │   ├── mapper
    │   │           │       │   │   └── AccountMapper.java      ← MapStruct
    │   │           │       │   └── repository
    │   │           │       │       └── JpaAccountRepository.java
    │   │           │       └── web
    │   │           │           ├── controller
    │   │           │           │   └── AccountController.java
    │   │           │           └── dto
    │   │           │               ├── AccountResponse.java
    │   │           │               └── CreateAccountRequest.java
    │   │           │
    │   │           ├── transactions                             ← subdominio CORE
    │   │           │   ├── application
    │   │           │   │   ├── dto
    │   │           │   │   │   └── TransferCommand.java
    │   │           │   │   ├── port                             ← puertos consumidos
    │   │           │   │   │   ├── AccountFundsPort.java        ← contrato hacia accounts
    │   │           │   │   │   ├── AccountSnapshot.java         ← DTO inmutable (record)
    │   │           │   │   │   └── NotificationPort.java
    │   │           │   │   └── usecase
    │   │           │   │       └── TransferMoneyUseCase.java    ← NO importa accounts/*
    │   │           │   └── infrastructure
    │   │           │       ├── acl                              ← ★ ANTI-CORRUPTION LAYER
    │   │           │       │   └── AccountsContextAdapter.java  ← único puente con accounts
    │   │           │       ├── notification
    │   │           │       │   └── ConsoleNotificationAdapter.java
    │   │           │       └── web
    │   │           │           ├── controller
    │   │           │           │   └── TransactionController.java
    │   │           │           └── dto
    │   │           │               └── TransferRequest.java
    │   │           │
    │   │           └── shared                                   ← subdominio SOPORTE
    │   │               ├── README.md
    │   │               ├── domain
    │   │               │   ├── exception
    │   │               │   │   └── DomainException.java
    │   │               │   └── model
    │   │               │       └── Money.java                   ← Value Object
    │   │               └── infrastructure
    │   │                   └── config
    │   │                       └── BeanConfiguration.java       ← wiring manual de @Bean
    │   └── resources
    │       └── application.yml
    └── test
        └── java
            └── com
                └── banco
                    ├── accounts
                    │   └── domain
                    │       └── model
                    │           └── BankAccountTest.java
                    ├── shared
                    │   └── domain
                    │       └── model
                    │           └── MoneyTest.java
                    └── transactions
                        └── application
                            └── usecase
                                └── TransferMoneyUseCaseTest.java   ← usa fakes de los ports
```

### Regla clave del ACL

> Dentro del subdominio `transactions`, **solo** los archivos bajo `transactions/infrastructure/acl/` pueden importar de `com.banco.accounts.*`. Un import de `com.banco.accounts.*` en cualquier otro lugar de `transactions/` es una violación de diseño, no de estilo.

Ver [ACL.md](ACL.md) para el diagrama de secuencia UML y la guía de extensión.
