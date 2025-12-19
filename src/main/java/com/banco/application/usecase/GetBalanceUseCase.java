package com.banco.application.usecase;

import com.banco.domain.exception.AccountNotFoundException;
import com.banco.domain.model.BankAccount;
import com.banco.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * USE CASE: Consultar saldo
 * 
 * @RequiredArgsConstructor: Lombok genera el constructor
 */
@RequiredArgsConstructor
public class GetBalanceUseCase {
    
    private final AccountRepository accountRepository;
    
    public BankAccount execute(String accountNumber) {
        return accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(
                "Account not found: " + accountNumber
            ));
    }
}
