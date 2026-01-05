package com.banco.shared.infrastructure.config;

import com.banco.accounts.application.usecase.CreateAccountUseCase;
import com.banco.accounts.application.usecase.GetBalanceUseCase;
import com.banco.transactions.application.usecase.TransferMoneyUseCase;
import com.banco.transactions.domain.model.Transfer;
import com.banco.accounts.domain.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * CONFIGURACIÓN DE BEANS
 * 
 * Registra los Use Cases y Domain Services como beans de Spring.
 * 
 * Nota: Lombok @RequiredArgsConstructor se encarga de la inyección,
 * aquí solo creamos las instancias.
 */
@Configuration
@EnableTransactionManagement
public class BeanConfiguration {
    
    @Bean
    public Transfer transferService() {
        return new Transfer();
    }
    
    @Bean
    public CreateAccountUseCase createAccountUseCase(AccountRepository accountRepository) {
        return new CreateAccountUseCase(accountRepository);
    }
    
    @Bean
    public TransferMoneyUseCase transferMoneyUseCase(
            AccountRepository accountRepository,
            Transfer transferService,
            TransferMoneyUseCase.NotificationPort notificationPort) {
        return new TransferMoneyUseCase(accountRepository, transferService, notificationPort);
    }
    
    @Bean
    public GetBalanceUseCase getBalanceUseCase(AccountRepository accountRepository) {
        return new GetBalanceUseCase(accountRepository);
    }
}
