package com.narender.installment.dto;

import com.narender.installment.domain.InstallmentPlan;
import com.narender.installment.domain.PlanStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** What the API returns for a plan — never exposes the entity directly. */
public record PlanResponse(
        UUID id,
        String cardId,
        BigDecimal purchaseAmount,
        int termMonths,
        BigDecimal apr,
        BigDecimal monthlyPayment,
        BigDecimal totalPayable,
        PlanStatus status,
        Instant createdAt
) {
    public static PlanResponse from(InstallmentPlan plan) {
        BigDecimal total = plan.getMonthlyPayment()
                .multiply(BigDecimal.valueOf(plan.getTermMonths()));
        return new PlanResponse(
                plan.getId(), plan.getCardId(), plan.getPurchaseAmount(),
                plan.getTermMonths(), plan.getApr(), plan.getMonthlyPayment(),
                total, plan.getStatus(), plan.getCreatedAt());
    }
}
