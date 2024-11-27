package io.github.purchase_approval_system.purchase_approval.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "purchase-approval.limits")
public class AppConfiguration {
    private Double minAmount;
    private Double maxAmount;
    private int minPeriod;
    private int maxPeriod;
    private double stepReduce;
}
