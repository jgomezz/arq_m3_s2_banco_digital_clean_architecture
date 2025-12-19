package com.banco.infrastructure.web.dto;

import com.banco.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida HTTP
 * 
 * @Builder: Patrón Builder para construcción fluida
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    
    private Long id;
    private String accountNumber;
    private String holderName;
    private String balance;
    private String status;
    
    /**
     * Factory method para crear desde BankAccount
     */
    public static AccountResponse from(BankAccount account) {
        return AccountResponse.builder()
            .id(account.getId())
            .accountNumber(account.getAccountNumber())
            .holderName(account.getHolderName())
            .balance(account.getBalance().toString())
            .status(account.getStatus().name())
            .build();
    }
}
