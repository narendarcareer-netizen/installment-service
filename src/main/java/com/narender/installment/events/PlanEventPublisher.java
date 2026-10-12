package com.narender.installment.events;

/** Abstraction so the demo runs with or without a Kafka broker. */
public interface PlanEventPublisher {
    void publish(PlanEvent event);
}
