package com.banco.infrastructure.web.controller;

import com.banco.application.dto.CreateAccountCommand;
import com.banco.application.dto.TransferCommand;
import com.banco.application.usecase.CreateAccountUseCase;
import com.banco.application.usecase.GetBalanceUseCase;
import com.banco.application.usecase.TransferMoneyUseCase;
import com.banco.domain.model.BankAccount;
import com.banco.infrastructure.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    private final TransferMoneyUseCase transferMoneyUseCase;
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
    
    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        TransferCommand command = new TransferCommand(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            request.getCurrency()
        );
        
        transferMoneyUseCase.execute(command);
        
        return ResponseEntity.ok("Transfer completed successfully");
    }
    
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getBalance(@PathVariable String accountNumber) {
        BankAccount account = getBalanceUseCase.execute(accountNumber);
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.ok(response);
    }
}
