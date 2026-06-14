package com.financetracker.auth.infrastructure.persistence;

import com.financetracker.auth.domain.RefreshToken;
import com.financetracker.auth.domain.RefreshTokenRepository;
import com.financetracker.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.financetracker.shared.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository repository;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(RefreshTokenJpaEntity::toRefreshToken);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return repository.save(RefreshTokenJpaEntity.fromRefreshToken(token)).toRefreshToken();
    }

    @Override
    @Transactional
    public void deleteByTokenHash(String tokenHash) {
        repository.deleteByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(UserId userId) {
        repository.deleteAllByUserId(userId.value());
    }
}
