package com.narender.installment.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The money math must be right — this is the one test that matters most
 * in a lending demo. $1,200 at 12% APR over 12 months is a textbook case:
 * the monthly payment is $106.62 (standard amortization).
 */
class InstallmentPlanServiceTest {

    @Test
    void monthlyPayment_matchesTextbookAmortization() {
        BigDecimal monthly = InstallmentPlanService.monthlyPayment(
                new BigDecimal("1200.00"), new BigDecimal("12.00"), 12);
        assertEquals(new BigDecimal("106.62"), monthly);
    }

    @Test
    void zeroApr_splitsEvenly() {
        BigDecimal monthly = InstallmentPlanService.monthlyPayment(
                new BigDecimal("1200.00"), BigDecimal.ZERO, 12);
        assertEquals(new BigDecimal("100.00"), monthly);
    }
}
