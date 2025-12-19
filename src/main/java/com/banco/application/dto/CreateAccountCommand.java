package com.banco.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * COMMAND: Datos de entrada para crear cuenta
 * 
 * Usando Lombok:
 * @AllArgsConstructor: Genera constructor con todos los campos
 * @Getter: Genera getters
 */
@Getter
@AllArgsConstructor
public class CreateAccountCommand {
    
    private final String holderName;
    private final BigDecimal initialBalance;
    private final String currency;
}
