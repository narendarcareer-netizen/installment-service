package com.narender.installment.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Default publisher: logs the event. Active when Kafka is disabled
 * (the default), so the demo runs anywhere with zero infrastructure.
 */
@Primary
@Component
@ConditionalOnProperty(name = "app.events.kafka-enabled",
        havingValue = "false", matchIfMissing = true)
public class LoggingPlanEventPublisher implements PlanEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingPlanEventPublisher.class);

    @Override
    public void publish(PlanEvent event) {
        log.info("plan-event type={} planId={} cardId={}",
                event.eventType(), event.planId(), event.cardId());
    }
}
