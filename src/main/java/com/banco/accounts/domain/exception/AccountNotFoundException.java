package com.banco.accounts.domain.exception;

import com.banco.shared.domain.exception.DomainException;

public class AccountNotFoundException extends DomainException {
    
    public AccountNotFoundException(String message) {
        super(message);
    }
}
