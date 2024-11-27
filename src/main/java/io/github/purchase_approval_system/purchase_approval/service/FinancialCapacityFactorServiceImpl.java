package io.github.purchase_approval_system.purchase_approval.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FinancialCapacityFactorServiceImpl implements FinancialCapacityFactorService {
    // Mocked profiles
    private static final Map<String, Integer> profiles = new HashMap<>();
    static {
        profiles.put("12345678901", null);  // Ineligible
        profiles.put("12345678912", 50);    // Profile 1
        profiles.put("12345678923", 100);   // Profile 2
        profiles.put("12345678934", 500);   // Profile 3
    }

    @Override
    public Integer getFactor(String personalId) {
        if (profiles.containsKey(personalId)) {
            return profiles.get(personalId);
        }
        return null;
    }
}
