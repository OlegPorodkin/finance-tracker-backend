package com.financetracker.users.infrastructure;

import com.financetracker.shared.domain.TransactionPort;
import com.financetracker.users.application.GetUserProfileUseCase;
import com.financetracker.users.application.UpdateUserUseCase;
import com.financetracker.users.domain.UserProfileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersUseCaseConfig {

    @Bean
    public GetUserProfileUseCase getUserProfileUseCase(UserProfileRepository userProfileRepository) {
        return new GetUserProfileUseCase(userProfileRepository);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserProfileRepository userProfileRepository,
                                                TransactionPort transactionPort) {
        return new UpdateUserUseCase(userProfileRepository, transactionPort);
    }
}
