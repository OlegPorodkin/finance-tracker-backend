package com.financetracker.users.infrastructure.persistence;

import com.financetracker.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.financetracker.auth.infrastructure.persistence.SpringDataUserRepository;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import com.financetracker.users.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUserProfileRepositoryAdapter implements UserProfileRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findById(UserId userId) {
        return repository.findById(userId.value()).map(UserJpaEntity::toUser);
    }

    @Override
    public User save(User user) {
        return repository.save(UserJpaEntity.fromUser(user)).toUser();
    }
}
