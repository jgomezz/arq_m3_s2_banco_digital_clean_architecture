package com.banco.domain.model;

import com.banco.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    
    @Test
    void shouldAddMoney() {
        Money money1 = Money.of(new BigDecimal("100"), "USD");
        Money money2 = Money.of(new BigDecimal("50"), "USD");
        
        Money result = money1.add(money2);
        
        assertEquals(Money.of(new BigDecimal("150"), "USD"), result);
    }
    
    @Test
    void shouldSubtractMoney() {
        Money money1 = Money.of(new BigDecimal("100"), "USD");
        Money money2 = Money.of(new BigDecimal("30"), "USD");
        
        Money result = money1.subtract(money2);
        
        assertEquals(Money.of(new BigDecimal("70"), "USD"), result);
    }
    
    @Test
    void shouldThrowExceptionWhenSubtractingMoreThanAvailable() {
        Money money1 = Money.of(new BigDecimal("50"), "USD");
        Money money2 = Money.of(new BigDecimal("100"), "USD");
        
        assertThrows(InsufficientFundsException.class, 
            () -> money1.subtract(money2)
        );
    }
    
    @Test
    void shouldThrowExceptionWhenDifferentCurrencies() {
        Money usd = Money.of(new BigDecimal("100"), "USD");
        Money eur = Money.of(new BigDecimal("50"), "EUR");
        
        assertThrows(IllegalArgumentException.class, 
            () -> usd.add(eur)
        );
    }
    
    @Test
    void shouldBeImmutable() {
        Money original = Money.of(new BigDecimal("100"), "USD");
        Money toAdd = Money.of(new BigDecimal("50"), "USD");
        
        Money result = original.add(toAdd);
        
        assertEquals(Money.of(new BigDecimal("100"), "USD"), original);
        assertEquals(Money.of(new BigDecimal("150"), "USD"), result);
    }
}
