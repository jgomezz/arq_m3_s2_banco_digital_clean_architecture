package com.banco.transactions.application.usecase;

import com.banco.shared.domain.model.Money;
import com.banco.transactions.application.dto.TransferCommand;
import com.banco.transactions.application.port.AccountFundsPort;
import com.banco.transactions.application.port.AccountSnapshot;
import com.banco.transactions.application.port.NotificationPort;
import lombok.RequiredArgsConstructor;

/**
 * USE CASE: Transferir dinero entre cuentas.
 *
 * ╔════════════════════════════════════════════════════════════════════════╗
 * ║  IMPORTANTE: este archivo NO importa NADA del paquete                  ║
 * ║  com.banco.accounts.                                                   ║
 * ║                                                                        ║
 * ║  Toda interacción con el contexto de cuentas pasa por el puerto        ║
 * ║  AccountFundsPort (Anti-Corruption Layer). El adapter del puerto       ║
 * ║  vive en transactions/infrastructure/acl/.                             ║
 * ╚════════════════════════════════════════════════════════════════════════╝
 */
@RequiredArgsConstructor
public class TransferMoneyUseCase {

    private final AccountFundsPort accountFundsPort;
    private final NotificationPort notificationPort;
    
    public void execute(TransferCommand command) {


        // 1. Lookup vía ACL: el use case nunca toca BankAccount directamente.
        AccountSnapshot from = accountFundsPort.lookup(command.getFromAccountNumber());
        AccountSnapshot to   = accountFundsPort.lookup(command.getToAccountNumber());


        // 2. Crear value object
        Money amount = Money.of(command.getAmount(), command.getCurrency());


        // 3. Mover los fondos vía ACL (validaciones cross-aggregate ocurren ahí)
        accountFundsPort.moveFunds(from.accountNumber(), to.accountNumber(), amount);


        // 4. Re-leer saldos actualizados para las notificaciones
        AccountSnapshot fromAfter = accountFundsPort.lookup(from.accountNumber());
        AccountSnapshot toAfter   = accountFundsPort.lookup(to.accountNumber());

        // 5. Notificar
        notifyTransfer(fromAfter, toAfter, amount);

    }
    
    private void notifyTransfer(AccountSnapshot from, AccountSnapshot to, Money amount) {

        notificationPort.notifyTransferSent(
            from.holderName(),
            amount.toString(),
            to.accountNumber(),
            from.balance().toString()
        );
        
        notificationPort.notifyTransferReceived(
            to.holderName(),
            amount.toString(),
            from.accountNumber(),
            to.balance().toString()
        );
    }

}
