package com.banco.domain.model;

/**
 * DOMAIN SERVICE: TransferService
 * 
 * Coordina la transferencia entre dos cuentas (dos aggregates).
 */
public class TransferService {
    
    public void transfer(BankAccount fromAccount, BankAccount toAccount, Money amount) {
        validateNotSameAccount(fromAccount, toAccount);
        validateSameCurrency(fromAccount, toAccount);
        
        // Ejecutar operaciones en ambos aggregates
        fromAccount.debit(amount);
        toAccount.credit(amount);
    }
    
    private void validateNotSameAccount(BankAccount from, BankAccount to) {
        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
    }
    
    private void validateSameCurrency(BankAccount from, BankAccount to) {
        if (!from.getBalance().getCurrency().equals(to.getBalance().getCurrency())) {
            throw new IllegalArgumentException(
                "Accounts have different currencies: " + 
                from.getBalance().getCurrency() + " vs " + 
                to.getBalance().getCurrency()
            );
        }
    }
}
