#  Banco Digital - Clean Architecture + DDD 

Sistema bancario simple que implementa **Domain-Driven Design (DDD)** y **Clean Architecture** con Spring Boot

## Funcionalidades

- Crear cuentas bancarias
- Transferir dinero entre cuentas
- Consultar saldo
- Validación de saldo suficiente
- Notificaciones por consola

## Estructura del Proyecto

```
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── banco
    │   │           ├── accounts
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
    │   │           │   │   │   └── BankAccount.java
    │   │           │   │   └── repository
    │   │           │   │       └── AccountRepository.java
    │   │           │   ├── infrastructure
    │   │           │   │   ├── notification
    │   │           │   │   │   └── ConsoleNotificationAdapter.java
    │   │           │   │   ├── persistence
    │   │           │   │   │   ├── adapter
    │   │           │   │   │   │   └── AccountRepositoryAdapter.java
    │   │           │   │   │   ├── entity
    │   │           │   │   │   │   └── AccountEntity.java
    │   │           │   │   │   ├── mapper
    │   │           │   │   │   │   └── AccountMapper.java
    │   │           │   │   │   └── repository
    │   │           │   │   │       └── JpaAccountRepository.java
    │   │           │   │   └── web
    │   │           │   │       ├── controller
    │   │           │   │       │   └── AccountController.java
    │   │           │   │       └── dto
    │   │           │   │           ├── AccountResponse.java
    │   │           │   │           └── CreateAccountRequest.java
    │   │           │   └── README.md
    │   │           ├── BancoDigitalApplication.java
    │   │           ├── shared
    │   │           │   ├── domain
    │   │           │   │   ├── exception
    │   │           │   │   │   └── DomainException.java
    │   │           │   │   └── model
    │   │           │   │       └── Money.java
    │   │           │   ├── infrastructure
    │   │           │   │   └── config
    │   │           │   │       └── BeanConfiguration.java
    │   │           │   └── README.md
    │   │           └── transactions
    │   │               ├── application
    │   │               │   ├── dto
    │   │               │   │   └── TransferCommand.java
    │   │               │   └── usecase
    │   │               │       └── TransferMoneyUseCase.java
    │   │               ├── domain
    │   │               │   ├── model
    │   │               │   │   └── Transfer.java
    │   │               │   └── repository
    │   │               ├── infrastructure
    │   │               │   ├── persistence
    │   │               │   └── web
    │   │               │       ├── controller
    │   │               │       │   └── TransactionController.java
    │   │               │       └── dto
    │   │               │           └── TransferRequest.java
    │   │               └── README.md
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
                    └── shared
                        └── domain
                            └── model
                                └── MoneyTest.java
```
