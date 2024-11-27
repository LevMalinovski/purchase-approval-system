package io.github.purchase_approval_system.purchase_approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PurchaseApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseApprovalApplication.class, args);
    }
}
