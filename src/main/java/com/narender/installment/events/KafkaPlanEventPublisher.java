package com.narender.installment.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Real publisher: sends the event to Kafka.
 * Enable with: app.events.kafka-enabled=true (needs a broker at
 * spring.kafka.bootstrap-servers).
 */
@Component
@ConditionalOnProperty(name = "app.events.kafka-enabled", havingValue = "true")
public class KafkaPlanEventPublisher implements PlanEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPlanEventPublisher.class);
    private static final String TOPIC = "installment-plan-events";

    private final KafkaTemplate<String, String> kafka;

    public KafkaPlanEventPublisher(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    @Override
    public void publish(PlanEvent event) {
        String payload = event.eventType() + "|" + event.planId() + "|" + event.cardId();
        kafka.send(TOPIC, event.planId().toString(), payload)
                .whenComplete((r, e) -> {
                    if (e != null) log.error("failed to publish plan-event", e);
                    else log.info("published plan-event to {}", TOPIC);
                });
    }
}
