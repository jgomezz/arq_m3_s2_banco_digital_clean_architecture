package com.banco.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * VALUE OBJECT: Money
 * 
 * Usando Lombok pero manteniendo validaciones y lógica de negocio.
 * 
 * @Value no se usa aquí porque necesitamos validaciones en constructor
 * y métodos de negocio personalizados.
 */
@Getter
@EqualsAndHashCode
public final class Money {
    
    private final BigDecimal amount;
    private final String currency;
    
    private Money(BigDecimal amount, String currency) {
        validateAmount(amount);
        validateCurrency(currency);
        
        this.amount = amount;
        this.currency = currency;
    }
    
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
    
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money subtract(Money other) {
        validateSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new com.banco.domain.exception.InsufficientFundsException(
                "Insufficient funds: " + this + " - " + other
            );
        }
        
        return new Money(result, this.currency);
    }
    
    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
    
    private void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be empty");
        }
    }
    
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Currency mismatch: " + this.currency + " vs " + other.currency
            );
        }
    }
    
    @Override
    public String toString() {
        return currency + " " + amount;
    }
}
