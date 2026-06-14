package com.financetracker.shared.infrastructure.persistence;

import com.financetracker.shared.domain.TransactionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SpringTransactionPortAdapter implements TransactionPort {

    private final PlatformTransactionManager transactionManager;

    @Override
    public <T> T execute(Supplier<T> operation) {
        return new TransactionTemplate(transactionManager).execute(status -> operation.get());
    }

    @Override
    public void execute(Runnable operation) {
        new TransactionTemplate(transactionManager).execute(status -> {
            operation.run();
            return null;
        });
    }
}
