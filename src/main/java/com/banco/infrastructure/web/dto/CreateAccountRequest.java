package com.banco.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de entrada HTTP para crear cuenta
 * 
 * @Data: Genera getters, setters, equals, hashCode, toString
 * @NoArgsConstructor: Constructor vacío para Jackson (deserialización JSON)
 * @AllArgsConstructor: Constructor con todos los campos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    
    private String holderName;
    private BigDecimal initialBalance;
    private String currency;
}
