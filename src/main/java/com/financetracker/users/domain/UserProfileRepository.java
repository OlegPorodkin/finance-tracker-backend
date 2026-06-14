package com.financetracker.users.domain;

import com.financetracker.shared.domain.User;
import com.financetracker.shared.domain.UserId;

import java.util.Optional;

public interface UserProfileRepository {

    Optional<User> findById(UserId userId);

    User save(User user);
}
