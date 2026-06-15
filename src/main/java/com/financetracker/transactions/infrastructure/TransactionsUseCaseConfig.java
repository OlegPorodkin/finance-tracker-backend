package com.financetracker.transactions.infrastructure;

import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.transactions.application.CreateTransactionUseCase;
import com.financetracker.transactions.application.DeleteTransactionUseCase;
import com.financetracker.transactions.application.ExportTransactionsUseCase;
import com.financetracker.transactions.application.GetTransactionUseCase;
import com.financetracker.transactions.application.GetTransactionsUseCase;
import com.financetracker.transactions.application.UpdateTransactionUseCase;
import com.financetracker.transactions.domain.TransactionRepository;
import com.financetracker.users.domain.UserProfileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionsUseCaseConfig {

    @Bean
    public GetTransactionsUseCase getTransactionsUseCase(TransactionRepository transactionRepository) {
        return new GetTransactionsUseCase(transactionRepository);
    }

    @Bean
    public GetTransactionUseCase getTransactionUseCase(TransactionRepository transactionRepository) {
        return new GetTransactionUseCase(transactionRepository);
    }

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(TransactionRepository transactionRepository,
                                                              UserProfileRepository userProfileRepository,
                                                              TransactionPort transactionPort) {
        return new CreateTransactionUseCase(transactionRepository, userProfileRepository, transactionPort);
    }

    @Bean
    public UpdateTransactionUseCase updateTransactionUseCase(TransactionRepository transactionRepository,
                                                              UserProfileRepository userProfileRepository,
                                                              TransactionPort transactionPort) {
        return new UpdateTransactionUseCase(transactionRepository, userProfileRepository, transactionPort);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(TransactionRepository transactionRepository,
                                                              TransactionPort transactionPort) {
        return new DeleteTransactionUseCase(transactionRepository, transactionPort);
    }

    @Bean
    public ExportTransactionsUseCase exportTransactionsUseCase(TransactionRepository transactionRepository) {
        return new ExportTransactionsUseCase(transactionRepository);
    }
}
