package com.financetracker.notifications.domain;

public interface NotificationPort {
    boolean sendBudgetAlert(String toEmail, String categoryName, int spentPercent, long limitCents, String currency);
}