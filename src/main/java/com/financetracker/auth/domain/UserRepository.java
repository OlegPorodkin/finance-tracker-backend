package com.financetracker.auth.domain;

import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UserId userId);

    boolean existsByEmail(String email);

    User save(User user);
}
