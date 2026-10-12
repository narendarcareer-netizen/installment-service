package com.narender.installment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** What the client sends to open a new installment plan. */
public record CreatePlanRequest(
        @NotBlank String cardId,
        @NotNull @DecimalMin("1.00") BigDecimal purchaseAmount,
        @Min(3) @Max(24) int termMonths,
        @NotNull @DecimalMin("0.00") @DecimalMax("36.00") BigDecimal apr
) {}
