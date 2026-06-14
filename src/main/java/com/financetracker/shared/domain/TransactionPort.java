package com.financetracker.shared.domain;

import java.util.function.Supplier;

public interface TransactionPort {

    <T> T execute(Supplier<T> operation);

    void execute(Runnable operation);
}
