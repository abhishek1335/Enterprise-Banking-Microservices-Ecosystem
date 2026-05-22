package com.banking.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "banking.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;

    /** Tokens added per second (sustained rate). */
    private int replenishPerSecond = 20;

    /** Maximum burst bucket size. */
    private int burstCapacity = 40;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getReplenishPerSecond() {
        return replenishPerSecond;
    }

    public void setReplenishPerSecond(int replenishPerSecond) {
        this.replenishPerSecond = replenishPerSecond;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }
}
