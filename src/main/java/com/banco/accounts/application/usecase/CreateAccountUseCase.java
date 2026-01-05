package com.banco.accounts.application.usecase;

import com.banco.accounts.application.dto.CreateAccountCommand;
import com.banco.accounts.domain.model.BankAccount;
import com.banco.shared.domain.model.Money;
import com.banco.accounts.domain.repository.AccountRepository;
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
