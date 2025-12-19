package com.banco.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * COMMAND: Datos de entrada para transferencia
 * 
 * Usando Lombok para reducir boilerplate
 */
@Getter
@AllArgsConstructor
public class TransferCommand {
    
    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final BigDecimal amount;
    private final String currency;
}
