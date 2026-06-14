--liquibase formatted sql

--changeset financetracker:002-seed-categories
INSERT INTO categories (id, user_id, name, type, color, icon, is_default, is_deleted) VALUES
    (gen_random_uuid(), NULL, 'Salary',          'INCOME',  '#22c55e', 'briefcase',    TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Freelance',        'INCOME',  '#16a34a', 'laptop',       TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Investment',       'INCOME',  '#15803d', 'trending-up',  TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Gift',             'INCOME',  '#4ade80', 'gift',         TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Other Income',     'INCOME',  '#86efac', 'plus-circle',  TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Housing',          'EXPENSE', '#ef4444', 'home',         TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Food & Groceries', 'EXPENSE', '#f97316', 'shopping-cart',TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Transport',        'EXPENSE', '#eab308', 'car',          TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Health',           'EXPENSE', '#3b82f6', 'heart-pulse',  TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Entertainment',    'EXPENSE', '#8b5cf6', 'tv-2',         TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Shopping',         'EXPENSE', '#ec4899', 'shopping-bag', TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Utilities',        'EXPENSE', '#06b6d4', 'zap',          TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Education',        'EXPENSE', '#f59e0b', 'book-open',    TRUE, FALSE),
    (gen_random_uuid(), NULL, 'Other Expense',    'EXPENSE', '#6b7280', 'more-horizontal', TRUE, FALSE);
