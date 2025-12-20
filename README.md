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
src/main/java/com/banco/
├── domain/                          # CAPA 1: DOMAIN
│   ├── model/
│   │   ├── Money.java               ← @Getter, @EqualsAndHashCode
│   │   ├── BankAccount.java         ← @Getter (sin @Setter públicos)
│   │   └── TransferService.java
│   ├── repository/
│   │   └── AccountRepository.java
│   └── exception/
│
├── application/                     # CAPA 2: APPLICATION
│   ├── usecase/
│   │   ├── CreateAccountUseCase.java       ← @RequiredArgsConstructor
│   │   ├── TransferMoneyUseCase.java       ← @RequiredArgsConstructor
│   │   └── GetBalanceUseCase.java          ← @RequiredArgsConstructor
│   └── dto/
│       ├── CreateAccountCommand.java       ← @Getter, @AllArgsConstructor
│       └── TransferCommand.java            ← @Getter, @AllArgsConstructor
│
└── infrastructure/                  # CAPAS 3 y 4
    ├── web/
    │   ├── controller/
    │   │   └── AccountController.java      ← @RequiredArgsConstructor
    │   └── dto/
    │       ├── CreateAccountRequest.java   ← @Data, @NoArgsConstructor
    │       ├── TransferRequest.java        ← @Data, @NoArgsConstructor
    │       └── AccountResponse.java        ← @Builder
    ├── persistence/
    │   ├── entity/
    │   │   └── AccountEntity.java          ← @Data, @Builder
    │   ├── repository/
    │   │   └── JpaAccountRepository.java
    │   ├── mapper/
    │   │   └── AccountMapper.java          ← @Mapper (MapStruct)
    │   └── adapter/
    │       └── AccountRepositoryAdapter.java  ← @RequiredArgsConstructor
    ├── notification/
    │   └── ConsoleNotificationAdapter.java
    └── config/
        └── BeanConfiguration.java
```
