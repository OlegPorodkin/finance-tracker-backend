package com.financetracker.auth.infrastructure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private Endpoint login = new Endpoint(5, 1);
    private Endpoint register = new Endpoint(5, 60);
    private Endpoint refresh = new Endpoint(30, 1);

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Endpoint getLogin() { return login; }
    public void setLogin(Endpoint login) { this.login = login; }

    public Endpoint getRegister() { return register; }
    public void setRegister(Endpoint register) { this.register = register; }

    public Endpoint getRefresh() { return refresh; }
    public void setRefresh(Endpoint refresh) { this.refresh = refresh; }

    public static class Endpoint {
        private int capacity;
        private int refillMinutes;

        public Endpoint() {}

        public Endpoint(int capacity, int refillMinutes) {
            this.capacity = capacity;
            this.refillMinutes = refillMinutes;
        }

        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }

        public int getRefillMinutes() { return refillMinutes; }
        public void setRefillMinutes(int refillMinutes) { this.refillMinutes = refillMinutes; }
    }
}
