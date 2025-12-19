package com.banco.application.usecase;

import com.banco.application.dto.CreateAccountCommand;
import com.banco.domain.model.BankAccount;
import com.banco.domain.model.Money;
import com.banco.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * USE CASE: Crear cuenta bancaria
 * 
 * @RequiredArgsConstructor: Genera constructor con campos final
 */
@RequiredArgsConstructor
public class CreateAccountUseCase {
    
    private final AccountRepository accountRepository;
    
    public Long execute(CreateAccountCommand command) {
        Money initialBalance = Money.of(
            command.getInitialBalance(),
            command.getCurrency()
        );
        
        BankAccount account = new BankAccount(
            command.getHolderName(),
            initialBalance
        );
        
        BankAccount savedAccount = accountRepository.save(account);
        
        return savedAccount.getId();
    }
}
