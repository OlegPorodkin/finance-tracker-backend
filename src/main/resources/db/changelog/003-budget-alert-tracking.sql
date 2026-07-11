--liquibase formatted sql

--changeset financetracker:003-budget-last-alert-sent-at
ALTER TABLE budgets
    ADD COLUMN last_alert_sent_at TIMESTAMPTZ;
