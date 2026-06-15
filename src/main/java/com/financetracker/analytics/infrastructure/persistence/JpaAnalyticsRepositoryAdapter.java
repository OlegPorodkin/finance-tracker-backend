package com.financetracker.analytics.infrastructure.persistence;

import com.financetracker.analytics.domain.AnalyticsRepository;
import com.financetracker.analytics.domain.CategoryAmount;
import com.financetracker.analytics.domain.MonthlyTotal;
import com.financetracker.shared.domain.UserId;
import com.financetracker.transactions.domain.TransactionType;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class JpaAnalyticsRepositoryAdapter implements AnalyticsRepository {

    private final EntityManager em;

    public JpaAnalyticsRepositoryAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public long sumByType(UserId userId, TransactionType type, LocalDate from, LocalDate to) {
        Object result = em.createNativeQuery("""
                        SELECT COALESCE(SUM(amount_in_cents), 0)
                        FROM transactions
                        WHERE user_id = :userId
                          AND type = :type
                          AND (CAST(:from AS DATE) IS NULL OR date >= CAST(:from AS DATE))
                          AND (CAST(:to   AS DATE) IS NULL OR date <= CAST(:to   AS DATE))
                        """)
                .setParameter("userId", userId.value())
                .setParameter("type", type.name())
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return ((Number) result).longValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CategoryAmount> sumByCategoryAndType(UserId userId, TransactionType type,
                                                      LocalDate from, LocalDate to) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT t.category_id::text,
                               COALESCE(c.name, 'Uncategorized'),
                               SUM(t.amount_in_cents)
                        FROM transactions t
                        LEFT JOIN categories c ON t.category_id = c.id
                        WHERE t.user_id = :userId
                          AND t.type = :type
                          AND (CAST(:from AS DATE) IS NULL OR t.date >= CAST(:from AS DATE))
                          AND (CAST(:to   AS DATE) IS NULL OR t.date <= CAST(:to   AS DATE))
                        GROUP BY t.category_id, c.name
                        ORDER BY SUM(t.amount_in_cents) DESC
                        """)
                .setParameter("userId", userId.value())
                .setParameter("type", type.name())
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        return rows.stream()
                .map(row -> new CategoryAmount(
                        (String) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MonthlyTotal> sumByMonth(UserId userId, int year) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT
                            CAST(EXTRACT(YEAR  FROM date) AS INTEGER),
                            CAST(EXTRACT(MONTH FROM date) AS INTEGER),
                            COALESCE(SUM(CASE WHEN type = 'INCOME'  THEN amount_in_cents ELSE 0 END), 0),
                            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_in_cents ELSE 0 END), 0)
                        FROM transactions
                        WHERE user_id = :userId
                          AND EXTRACT(YEAR FROM date) = :year
                        GROUP BY EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date)
                        ORDER BY EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date)
                        """)
                .setParameter("userId", userId.value())
                .setParameter("year", year)
                .getResultList();

        return rows.stream()
                .map(row -> new MonthlyTotal(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }
}
