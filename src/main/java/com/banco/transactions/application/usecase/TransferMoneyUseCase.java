package com.banco.transactions.application.usecase;

import com.banco.transactions.application.dto.TransferCommand;
import com.banco.accounts.domain.exception.AccountNotFoundException;
import com.banco.accounts.domain.model.BankAccount;
import com.banco.shared.domain.model.Money;
import com.banco.transactions.domain.model.Transfer;
import com.banco.accounts.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * USE CASE: Transferir dinero entre cuentas
 * 
 * @RequiredArgsConstructor: Genera constructor para inyección de dependencias
 */
@RequiredArgsConstructor
public class TransferMoneyUseCase {
    
    private final AccountRepository accountRepository;
    private final Transfer transferService;
    private final NotificationPort notificationPort;
    
    public void execute(TransferCommand command) {
        // 1. Cargar cuentas
        BankAccount fromAccount = accountRepository
            .findByAccountNumber(command.getFromAccountNumber())
            .orElseThrow(() -> new AccountNotFoundException(
                "From account not found: " + command.getFromAccountNumber()
            ));
        
        BankAccount toAccount = accountRepository
            .findByAccountNumber(command.getToAccountNumber())
            .orElseThrow(() -> new AccountNotFoundException(
                "To account not found: " + command.getToAccountNumber()
            ));
        
        // 2. Crear value object
        Money amount = Money.of(command.getAmount(), command.getCurrency());
        
        // 3. Ejecutar transferencia
        transferService.transfer(fromAccount, toAccount, amount);
        
        // 4. Persistir
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // 5. Notificar
        notifyTransfer(fromAccount, toAccount, amount);
    }
    
    private void notifyTransfer(BankAccount from, BankAccount to, Money amount) {
        notificationPort.notifyTransferSent(
            from.getHolderName(),
            amount.toString(),
            to.getAccountNumber(),
            from.getBalance().toString()
        );
        
        notificationPort.notifyTransferReceived(
            to.getHolderName(),
            amount.toString(),
            from.getAccountNumber(),
            to.getBalance().toString()
        );
    }
    
    /**
     * PORT: Interface para notificaciones
     */
    public interface NotificationPort {
        void notifyTransferSent(String holderName, String amount, 
                               String toAccount, String newBalance);
        
        void notifyTransferReceived(String holderName, String amount,
                                   String fromAccount, String newBalance);
    }
}
