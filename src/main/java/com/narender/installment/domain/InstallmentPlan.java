package com.narender.installment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * One installment plan against a card account: a purchase amount split into
 * equal monthly payments over a fixed term, at a fixed APR.
 *
 * This is the "ledger" of the demo — the durable record every event and
 * API response is derived from.
 */
@Entity
@Table(name = "installment_plan")
public class InstallmentPlan {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String cardId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(nullable = false)
    private int termMonths;

    /** Annual percentage rate, e.g. 12.99 means 12.99%. */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal apr;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected InstallmentPlan() {
        // JPA only
    }

    public InstallmentPlan(String cardId, BigDecimal purchaseAmount,
                           int termMonths, BigDecimal apr, BigDecimal monthlyPayment) {
        this.id = UUID.randomUUID();
        this.cardId = cardId;
        this.purchaseAmount = purchaseAmount;
        this.termMonths = termMonths;
        this.apr = apr;
        this.monthlyPayment = monthlyPayment;
        this.status = PlanStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void cancel() {
        if (this.status != PlanStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE plans can be cancelled");
        }
        this.status = PlanStatus.CANCELLED;
    }

    public UUID getId() { return id; }
    public String getCardId() { return cardId; }
    public BigDecimal getPurchaseAmount() { return purchaseAmount; }
    public int getTermMonths() { return termMonths; }
    public BigDecimal getApr() { return apr; }
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public PlanStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
