package com.banco.shared.infrastructure.config;

import com.banco.accounts.application.usecase.CreateAccountUseCase;
import com.banco.accounts.application.usecase.GetBalanceUseCase;
import com.banco.transactions.application.port.AccountFundsPort;
import com.banco.transactions.application.port.NotificationPort;
import com.banco.transactions.application.usecase.TransferMoneyUseCase;
import com.banco.accounts.domain.repository.AccountRepository;
import com.banco.transactions.infrastructure.acl.AccountsContextAdapter;
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


    // ───── Casos de uso del contexto ACCOUNTS ─────

    @Bean
    public CreateAccountUseCase createAccountUseCase(AccountRepository accountRepository) {
        return new CreateAccountUseCase(accountRepository);
    }

    @Bean
    public GetBalanceUseCase getBalanceUseCase(AccountRepository accountRepository) {
        return new GetBalanceUseCase(accountRepository);
    }

    // ───── ACL: implementación del puerto AccountFundsPort ─────

    @Bean
    public AccountFundsPort accountFundsPort(AccountRepository accountRepository) {
        return new AccountsContextAdapter(accountRepository);
    }

    // ───── Caso de uso del contexto TRANSACTIONS ─────
    // Recibe el PUERTO (no AccountRepository): el use case no conoce el otro contexto.
    @Bean
    public TransferMoneyUseCase transferMoneyUseCase(
            AccountFundsPort accountFundsPort,
            NotificationPort notificationPort) {
        return new TransferMoneyUseCase(accountFundsPort, notificationPort);
    }

}
