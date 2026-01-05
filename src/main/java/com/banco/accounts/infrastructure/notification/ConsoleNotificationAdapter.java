package com.banco.accounts.infrastructure.notification;

import com.banco.transactions.application.usecase.TransferMoneyUseCase;
import org.springframework.stereotype.Component;

/**
 * ADAPTER: Implementa NotificationPort usando consola
 */
@Component
public class ConsoleNotificationAdapter implements TransferMoneyUseCase.NotificationPort {
    
    @Override
    public void notifyTransferSent(String holderName, String amount, 
                                   String toAccount, String newBalance) {
        String message = String.format("""
            
            ═══════════════════════════════════════════════
            NOTIFICACIÓN - TRANSFERENCIA ENVIADA
            ═══════════════════════════════════════════════
            Estimado/a %s:
            
            ✓ Se ha realizado una transferencia desde su cuenta
            
            Monto enviado:    %s
            Cuenta destino:   %s
            Su nuevo saldo:   %s
            
            Gracias por usar Banco Digital
            ═══════════════════════════════════════════════
            """, holderName, amount, toAccount, newBalance);
        
        System.out.println(message);
    }
    
    @Override
    public void notifyTransferReceived(String holderName, String amount,
                                       String fromAccount, String newBalance) {
        String message = String.format("""
            
            ═══════════════════════════════════════════════
            NOTIFICACIÓN - TRANSFERENCIA RECIBIDA
            ═══════════════════════════════════════════════
            Estimado/a %s:
            
            ✓ Ha recibido una transferencia en su cuenta
            
            Monto recibido:   %s
            Cuenta origen:    %s
            Su nuevo saldo:   %s
            
            Gracias por usar Banco Digital
            ═══════════════════════════════════════════════
            """, holderName, amount, fromAccount, newBalance);
        
        System.out.println(message);
    }
}
