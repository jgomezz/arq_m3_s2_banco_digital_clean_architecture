package com.banco.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de entrada HTTP para transferencia
 * 
 * Lombok reduce el boilerplate code significativamente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String currency;
}
