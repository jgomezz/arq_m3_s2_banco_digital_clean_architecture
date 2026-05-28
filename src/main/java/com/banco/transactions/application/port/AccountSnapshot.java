package com.banco.transactions.application.port;

import com.banco.shared.domain.model.Money;

/**
 * Vista mínima de una cuenta que el contexto de TRANSFERENCIAS necesita.
 *
 * NO es la entidad BankAccount — es un "snapshot" inmutable con solo los
 * datos que transferencias utiliza. Vive en el contexto de transactions
 * y habla en SU vocabulario.
 */
public record AccountSnapshot(
        String accountNumber,
        String holderName,
        Money balance
) {}