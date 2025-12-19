package com.banco.infrastructure.persistence.repository;

import com.banco.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository
 */
@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
    
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
