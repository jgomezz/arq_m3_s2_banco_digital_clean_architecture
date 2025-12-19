package com.banco.domain.model;

import com.banco.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {
    
    @Test
    void shouldCreateAccountWithInitialBalance() {
        Money initialBalance = Money.of(new BigDecimal("1000"), "USD");
        
        BankAccount account = new BankAccount("Alice Smith", initialBalance);
        
        assertNotNull(account.getAccountNumber());
        assertEquals("Alice Smith", account.getHolderName());
        assertEquals(initialBalance, account.getBalance());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }
    
    @Test
    void shouldDebitMoney() {
        Money initialBalance = Money.of(new BigDecimal("1000"), "USD");
        BankAccount account = new BankAccount("Alice", initialBalance);
        
        Money debitAmount = Money.of(new BigDecimal("300"), "USD");
        account.debit(debitAmount);
        
        assertEquals(Money.of(new BigDecimal("700"), "USD"), account.getBalance());
    }
    
    @Test
    void shouldCreditMoney() {
        Money initialBalance = Money.of(new BigDecimal("1000"), "USD");
        BankAccount account = new BankAccount("Alice", initialBalance);
        
        Money creditAmount = Money.of(new BigDecimal("500"), "USD");
        account.credit(creditAmount);
        
        assertEquals(Money.of(new BigDecimal("1500"), "USD"), account.getBalance());
    }
    
    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        Money initialBalance = Money.of(new BigDecimal("100"), "USD");
        BankAccount account = new BankAccount("Alice", initialBalance);
        
        Money debitAmount = Money.of(new BigDecimal("500"), "USD");
        
        assertThrows(InsufficientFundsException.class, 
            () -> account.debit(debitAmount)
        );
    }
    
    @Test
    void shouldThrowExceptionWhenCurrencyMismatch() {
        Money initialBalance = Money.of(new BigDecimal("1000"), "USD");
        BankAccount account = new BankAccount("Alice", initialBalance);
        
        Money euroAmount = Money.of(new BigDecimal("100"), "EUR");
        
        assertThrows(IllegalArgumentException.class, 
            () -> account.debit(euroAmount)
        );
    }
}
