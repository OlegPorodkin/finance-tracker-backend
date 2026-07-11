package com.financetracker.notifications.infrastructure;

import com.financetracker.notifications.domain.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.notifications.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ResendEmailAdapter implements NotificationPort {

    private final RestClient resendRestClient;

    @Value("${app.notifications.from-email}")
    private String fromEmail;

    @Override
    public boolean sendBudgetAlert(String toEmail, String categoryName, int spentPercent,
                                   long limitCents, String currency) {
        String subject = "Budget Alert: %s reached %d%%".formatted(categoryName, spentPercent);
        String html = buildAlertHtml(categoryName, spentPercent, limitCents, currency);
        try {
            resendRestClient.post()
                    .uri("/emails")
                    .body(new EmailRequest(fromEmail, List.of(toEmail), subject, html))
                    .retrieve()
                    .toBodilessEntity();
            log.debug("Budget alert sent to {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send budget alert to {}", toEmail, e);
            return false;
        }
    }

    private String buildAlertHtml(String categoryName, int spentPercent, long limitCents, String currency) {
        long limitUnits = limitCents / 100;
        return """
                <div style="font-family:sans-serif;max-width:600px;margin:0 auto;padding:24px;">
                  <h2 style="color:#e53e3e;">Budget Alert</h2>
                  <p>Your <strong>%s</strong> budget has reached <strong>%d%%</strong>
                     of the %s %,d limit.</p>
                  <p>Consider reviewing your spending to stay on track.</p>
                  <p style="color:#718096;font-size:0.875rem;margin-top:32px;">Finance Tracker</p>
                </div>
                """.formatted(categoryName, spentPercent, currency, limitUnits);
    }

    record EmailRequest(String from, List<String> to, String subject, String html) {}
}
