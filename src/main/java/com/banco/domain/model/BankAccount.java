package com.banco.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * AGGREGATE ROOT: BankAccount
 * 
 * Usando Lombok para reducir boilerplate, pero manteniendo:
 * - Encapsulación (NO @Setter públicos)
 * - Validaciones
 * - Lógica de negocio
 * 
 * @Getter: Genera getters para todos los campos
 * @Setter(AccessLevel.PROTECTED): Solo el ID puede modificarse (para JPA)
 */
@Getter
public class BankAccount {
    
    @Setter(AccessLevel.PUBLIC) // Solo para JPA
    private Long id;
    
    private String accountNumber;
    private String holderName;
    private Money balance;  //, VO
    private AccountStatus status;
    
    // Constructor para crear nueva cuenta
    public BankAccount(String holderName, Money initialBalance) {
        validateHolderName(holderName);
        validateInitialBalance(initialBalance);
        
        this.accountNumber = generateAccountNumber();
        this.holderName = holderName;
        this.balance = initialBalance;
        this.status = AccountStatus.ACTIVE;
    }
    
    // Constructor para reconstruir desde BD (usado por MapStruct)
    public BankAccount(Long id, String accountNumber, String holderName, 
                      Money balance, AccountStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.status = status;
    }
    
    // Constructor protegido para JPA
    protected BankAccount() {}
    
    // Métodos PACKAGE-PRIVATE (solo TransferService puede llamarlos)
    void debit(Money amount) {
        validateActive();
        validateCurrency(amount);
        this.balance = balance.subtract(amount);
    }
    
    void credit(Money amount) {
        validateActive();
        validateCurrency(amount);
        this.balance = balance.add(amount);
    }
    
    private void validateActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active: " + status);
        }
    }
    
    private void validateCurrency(Money amount) {
        if (!balance.getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch: account uses " + balance.getCurrency() + 
                " but operation uses " + amount.getCurrency()
            );
        }
    }
    
    private void validateHolderName(String holderName) {
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Holder name is required");
        }
    }
    
    private void validateInitialBalance(Money initialBalance) {
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance is required");
        }
    }
    
    private String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis());
    }
}
