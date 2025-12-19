package com.banco.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * JPA ENTITY (tabla de BD)
 * 
 * Usando Lombok:
 * @Data: Genera getters, setters, equals, hashCode, toString
 * @Builder: Patrón Builder
 * @NoArgsConstructor: Constructor vacío para JPA
 * @AllArgsConstructor: Constructor con todos los campos para Builder
 */
@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String accountNumber;
    
    @Column(nullable = false)
    private String holderName;
    
    @Column(nullable = false)
    private BigDecimal balanceAmount;
    
    @Column(nullable = false)
    private String balanceCurrency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;
}
