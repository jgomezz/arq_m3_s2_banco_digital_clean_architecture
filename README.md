# 🏦 Banco Digital - Clean Architecture + DDD + Lombok + MapStruct

Sistema bancario simple que implementa **Domain-Driven Design (DDD)** y **Clean Architecture** con Spring Boot, optimizado con **Lombok** y **MapStruct**.

## ✨ Funcionalidades

- ✅ Crear cuentas bancarias
- ✅ Transferir dinero entre cuentas
- ✅ Consultar saldo
- ✅ Validación de saldo suficiente
- ✅ **Notificaciones por consola**



**Ventajas:**
- ✅ Código generado en compile-time (no reflexión en runtime)
- ✅ Type-safe (errores en compilación, no en ejecución)
- ✅ Mantenible (cambios en el modelo se detectan automáticamente)
- ✅ Performance (no overhead de runtime)

## 📁 Estructura del Proyecto

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

## 🚀 Cómo Ejecutar

### 1. Compilar (MapStruct genera código aquí)
```bash
mvn clean install
```

### 2. Ejecutar
```bash
mvn spring-boot:run
```

### 3. Probar
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "Alice Smith",
    "initialBalance": 1000.00,
    "currency": "USD"
  }'
```

## 🎉 Resumen

Este proyecto demuestra cómo **Lombok** y **MapStruct** pueden:
- ✅ Reducir significativamente el boilerplate code
- ✅ Mantener la arquitectura limpia y los principios DDD
- ✅ Mejorar la mantenibilidad sin sacrificar rendimiento
- ✅ Generar código type-safe en compile-time
