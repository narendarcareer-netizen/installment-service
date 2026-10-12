package com.narender.installment.service;

import java.util.UUID;

public class PlanNotFoundException extends RuntimeException {
    public PlanNotFoundException(UUID id) {
        super("Installment plan not found: " + id);
    }
}
