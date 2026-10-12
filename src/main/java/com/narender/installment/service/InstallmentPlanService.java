package com.narender.installment.service;

import com.narender.installment.domain.InstallmentPlan;
import com.narender.installment.dto.CreatePlanRequest;
import com.narender.installment.dto.PlanResponse;
import com.narender.installment.events.PlanEvent;
import com.narender.installment.events.PlanEventPublisher;
import com.narender.installment.repo.InstallmentPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Business logic for installment plans. The key calculation is the standard
 * loan amortization formula:
 *
 *   monthly = P * r / (1 - (1 + r)^-n)
 *
 * where P = purchase amount, r = monthly rate (APR/12), n = term in months.
 */
@Service
public class InstallmentPlanService {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    private final InstallmentPlanRepository repository;
    private final PlanEventPublisher events;

    public InstallmentPlanService(InstallmentPlanRepository repository,
                                  PlanEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public PlanResponse createPlan(CreatePlanRequest request) {
        BigDecimal monthly = monthlyPayment(
                request.purchaseAmount(), request.apr(), request.termMonths());
        InstallmentPlan plan = new InstallmentPlan(
                request.cardId(), request.purchaseAmount(),
                request.termMonths(), request.apr(), monthly);
        InstallmentPlan saved = repository.save(plan);
        events.publish(new PlanEvent("PLAN_CREATED", saved.getId(), saved.getCardId()));
        return PlanResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PlanResponse getPlan(UUID id) {
        return repository.findById(id)
                .map(PlanResponse::from)
                .orElseThrow(() -> new PlanNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> plansForCard(String cardId) {
        return repository.findByCardId(cardId).stream()
                .map(PlanResponse::from)
                .toList();
    }

    @Transactional
    public PlanResponse cancelPlan(UUID id) {
        InstallmentPlan plan = repository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException(id));
        plan.cancel();
        InstallmentPlan saved = repository.save(plan);
        events.publish(new PlanEvent("PLAN_CANCELLED", saved.getId(), saved.getCardId()));
        return PlanResponse.from(saved);
    }

    /** Standard amortization formula, rounded to cents. Package-visible for tests. */
    static BigDecimal monthlyPayment(BigDecimal principal, BigDecimal apr, int months) {
        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal monthlyRate = apr.divide(BigDecimal.valueOf(100), MC)
                .divide(BigDecimal.valueOf(12), MC);
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate, MC);
        BigDecimal discount = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(onePlusR.pow(months, MC), MC), MC);
        return principal.multiply(monthlyRate, MC)
                .divide(discount, MC)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
