package com.banco.transactions.application.port;

import com.banco.shared.domain.model.Money;

/**
 * ANTI-CORRUPTION LAYER PORT
 *
 * Lo que el contexto de TRANSFERENCIAS necesita pedirle al contexto de
 * CUENTAS, expresado en el vocabulario de transferencias.
 *
 * El use case de transferencias depende SOLO de esta interfaz. NO conoce
 * BankAccount, ni AccountRepository, ni AccountNotFoundException.
 *
 * La implementación (el adapter del ACL) vive en
 * transactions/infrastructure/acl/.
 */
public interface AccountFundsPort {

    /** Trae los datos de una cuenta. */
    AccountSnapshot lookup(String accountNumber);

    /** Mueve fondos entre dos cuentas de forma coordinada. */
    void moveFunds(String fromAccountNumber, String toAccountNumber, Money amount);
}