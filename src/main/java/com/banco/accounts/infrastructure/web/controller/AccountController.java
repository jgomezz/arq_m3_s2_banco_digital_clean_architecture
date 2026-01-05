package com.banco.accounts.infrastructure.web.controller;

import com.banco.accounts.application.dto.CreateAccountCommand;
import com.banco.accounts.application.usecase.CreateAccountUseCase;
import com.banco.accounts.application.usecase.GetBalanceUseCase;
import com.banco.accounts.domain.model.BankAccount;
import com.banco.accounts.infrastructure.web.dto.AccountResponse;
import com.banco.accounts.infrastructure.web.dto.CreateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST CONTROLLER
 * 
 * @RequiredArgsConstructor: Lombok genera constructor para inyección de dependencias
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final CreateAccountUseCase createAccountUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    
    @PostMapping
    public ResponseEntity<Long> createAccount(@RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = new CreateAccountCommand(
            request.getHolderName(),
            request.getInitialBalance(),
            request.getCurrency()
        );
        
        Long accountId = createAccountUseCase.execute(command);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }

    
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getBalance(@PathVariable String accountNumber) {
        BankAccount account = getBalanceUseCase.execute(accountNumber);
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.ok(response);
    }
}
