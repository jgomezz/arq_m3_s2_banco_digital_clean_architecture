package com.banco.accounts.infrastructure.persistence.mapper;

import com.banco.accounts.domain.model.AccountStatus;
import com.banco.accounts.domain.model.BankAccount;
import com.banco.shared.domain.model.Money;
import com.banco.accounts.infrastructure.persistence.entity.AccountEntity;
import org.mapstruct.*;

/**
 * MAPPER: Convierte entre BankAccount (domain) y AccountEntity (JPA)
 * 
 * MapStruct genera automáticamente la implementación en tiempo de compilación.
 * 
 * @Mapper: Indica que es un mapper de MapStruct
 * componentModel = "spring": Registra el mapper como un bean de Spring
 * injectionStrategy = CONSTRUCTOR: Usa inyección por constructor
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

    // toEntity() funciona normal (AccountEntity SÍ tiene setters)
    @Mapping(target = "balanceAmount", expression = "java(account.getBalance().getAmount())")
    @Mapping(target = "balanceCurrency", expression = "java(account.getBalance().getCurrency())")
    @Mapping(target = "status", expression = "java(account.getStatus().name())")
    AccountEntity toEntity(BankAccount account);

    // toDomain() usa método default que llama al constructor ✅
    default BankAccount toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        Money balance = Money.of(
                entity.getBalanceAmount(),
                entity.getBalanceCurrency()
        );

        AccountStatus status = AccountStatus.valueOf(entity.getStatus());

        // Usa el constructor en lugar de setters
        return new BankAccount(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getHolderName(),
                balance,
                status
        );
    }
}