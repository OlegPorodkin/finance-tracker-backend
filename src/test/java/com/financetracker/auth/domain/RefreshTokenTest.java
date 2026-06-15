package com.financetracker.auth.domain;

import com.financetracker.shared.domain.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    private final UserId userId = UserId.generate();

    @Test
    void is_not_expired_when_expiry_is_in_the_future() {
        RefreshToken token = RefreshToken.create("hash", userId, Instant.now().plus(1, ChronoUnit.HOURS));
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void is_expired_when_expiry_is_in_the_past() {
        RefreshToken token = RefreshToken.create("hash", userId, Instant.now().minus(1, ChronoUnit.SECONDS));
        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void create_assigns_random_id_and_sets_fields() {
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
        RefreshToken token = RefreshToken.create("myhash", userId, expiresAt);

        assertThat(token.getId()).isNotNull();
        assertThat(token.getTokenHash()).isEqualTo("myhash");
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
    }
}
