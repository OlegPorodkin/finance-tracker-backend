package com.financetracker.notifications.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@Configuration
@EnableScheduling
public class NotificationConfig {

    @Bean
    @ConditionalOnProperty(name = "app.notifications.enabled", havingValue = "true")
    public RestClient resendRestClient(@Value("${app.notifications.resend-api-key}") String apiKey,
                                       @Value("${app.notifications.resend-base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}