package com.banco.transactions.application.usecase;

import com.banco.shared.domain.model.Money;
import com.banco.transactions.application.dto.TransferCommand;
import com.banco.transactions.application.port.AccountFundsPort;
import com.banco.transactions.application.port.AccountSnapshot;
import com.banco.transactions.application.port.NotificationPort;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TransferMoneyUseCaseTest {

    @Test
    void transferenciaExitosa() {
        // ────────────── ARRANGE ──────────────
        Map<String, AccountSnapshot> cuentas = new HashMap<>();
        cuentas.put("ALICE", new AccountSnapshot(
                "ALICE", "Alice", Money.of(new BigDecimal("1000"), "USD")));
        cuentas.put("BOB", new AccountSnapshot(
                "BOB", "Bob", Money.of(new BigDecimal("500"), "USD")));

        TransferMoneyUseCase useCase = getTransferMoneyUseCase(cuentas);

        // ────────────── ACT ──────────────
        TransferCommand cmd = new TransferCommand(
                "ALICE", "BOB", new BigDecimal("200"), "USD");
        useCase.execute(cmd);

        // ────────────── ASSERT ──────────────
        assertThat(cuentas.get("ALICE").balance().getAmount())
                .isEqualByComparingTo("800");
        assertThat(cuentas.get("BOB").balance().getAmount())
                .isEqualByComparingTo("700");
    }

    @Nonnull
    private static TransferMoneyUseCase getTransferMoneyUseCase(Map<String, AccountSnapshot> cuentas) {
        AccountFundsPort fakeAccountFundsPort = new AccountFundsPort() {
            @Override
            public AccountSnapshot lookup(String number) {
                return cuentas.get(number);
            }
            @Override
            public void moveFunds(String from, String to, Money amount) {
                AccountSnapshot f = cuentas.get(from);
                AccountSnapshot t = cuentas.get(to);
                cuentas.put(from, new AccountSnapshot(
                        f.accountNumber(), f.holderName(), f.balance().subtract(amount)));
                cuentas.put(to, new AccountSnapshot(
                        t.accountNumber(), t.holderName(), t.balance().add(amount)));
            }
        };

        NotificationPort fakeNotificationPort = new NotificationPort() {
            @Override public void notifyTransferSent(String h, String a, String t, String b) {}
            @Override public void notifyTransferReceived(String h, String a, String f, String b) {}
        };

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(
                fakeAccountFundsPort, fakeNotificationPort);
        return useCase;
    }
}