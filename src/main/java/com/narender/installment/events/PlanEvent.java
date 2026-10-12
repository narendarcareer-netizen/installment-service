package com.narender.installment.events;

import java.util.UUID;

/** Domain event published whenever a plan's lifecycle changes. */
public record PlanEvent(
        String eventType,   // PLAN_CREATED | PLAN_CANCELLED
        UUID planId,
        String cardId
) {}
