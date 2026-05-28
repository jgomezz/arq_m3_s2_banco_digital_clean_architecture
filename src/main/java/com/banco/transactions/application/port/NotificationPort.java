package com.banco.transactions.application.port;

/**
 * DRIVEN PORT: Notificaciones de transferencias.
 *
 * El adapter concreto (consola, email, SMS…) vive en infrastructure.
 */
public interface NotificationPort {

    void notifyTransferSent(String holderName, String amount,
                            String toAccount, String newBalance);

    void notifyTransferReceived(String holderName, String amount,
                                String fromAccount, String newBalance);
}