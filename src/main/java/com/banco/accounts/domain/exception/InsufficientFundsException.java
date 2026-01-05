package com.banco.accounts.domain.exception;

import com.banco.shared.domain.exception.DomainException;

public class InsufficientFundsException extends DomainException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
}
