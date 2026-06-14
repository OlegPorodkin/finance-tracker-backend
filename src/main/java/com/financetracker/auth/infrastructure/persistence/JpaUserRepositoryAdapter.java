package com.financetracker.auth.infrastructure.persistence;

import com.financetracker.auth.domain.UserRepository;
import com.financetracker.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserJpaEntity::toUser);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return repository.findById(userId.value()).map(UserJpaEntity::toUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return repository.save(UserJpaEntity.fromUser(user)).toUser();
    }
}
