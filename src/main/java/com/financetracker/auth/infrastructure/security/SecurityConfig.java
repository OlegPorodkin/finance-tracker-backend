package com.financetracker.auth.infrastructure.security;

import com.financetracker.auth.domain.TokenService;
import com.financetracker.auth.infrastructure.ratelimit.LoginRateLimitFilter;
import com.financetracker.auth.infrastructure.ratelimit.RateLimitProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenService tokenService) {
        return new JwtAuthenticationFilter(tokenService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<LoginRateLimitFilter> loginRateLimitFilter(RateLimitProperties props) {
        FilterRegistrationBean<LoginRateLimitFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginRateLimitFilter(
                props.getLogin().getCapacity(),
                props.getLogin().getRefillMinutes()));
        bean.addUrlPatterns("/api/v1/auth/login");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<LoginRateLimitFilter> registerRateLimitFilter(RateLimitProperties props) {
        FilterRegistrationBean<LoginRateLimitFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginRateLimitFilter(
                props.getRegister().getCapacity(),
                props.getRegister().getRefillMinutes()));
        bean.addUrlPatterns("/api/v1/auth/register");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<LoginRateLimitFilter> refreshRateLimitFilter(RateLimitProperties props) {
        FilterRegistrationBean<LoginRateLimitFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginRateLimitFilter(
                props.getRefresh().getCapacity(),
                props.getRefresh().getRefillMinutes()));
        bean.addUrlPatterns("/api/v1/auth/refresh");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
