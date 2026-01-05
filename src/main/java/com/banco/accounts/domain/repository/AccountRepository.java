package com.banco.accounts.domain.repository;

import com.banco.accounts.domain.model.BankAccount;
import java.util.Optional;

/**
 * REPOSITORY INTERFACE
 */
public interface AccountRepository {
    
    BankAccount save(BankAccount account);
    
    Optional<BankAccount> findById(Long id);
    
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
