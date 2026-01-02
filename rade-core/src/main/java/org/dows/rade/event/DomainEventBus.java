package org.dows.rade.event;

public interface DomainEventBus {
    void publish(DomainEvent event);
}