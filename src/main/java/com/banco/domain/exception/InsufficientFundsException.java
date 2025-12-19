package com.banco.domain.exception;

public class InsufficientFundsException extends DomainException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
}
