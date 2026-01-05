package com.banco.transactions.infrastructure.web.controller;

import com.banco.transactions.application.dto.TransferCommand;
import com.banco.transactions.application.usecase.TransferMoneyUseCase;
import com.banco.transactions.infrastructure.web.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST CONTROLLER
 * 
 * @RequiredArgsConstructor: Lombok genera constructor para inyección de dependencias
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransferMoneyUseCase transferMoneyUseCase;

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

}
