package org.dows.rade.event;

import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class VertxDomainEventBus implements DomainEventBus {

    private final Vertx vertx;
    @Override
    public void publish(DomainEvent event) {
        //String address = EventAddressMapper.map(event);
        //String jsonString = Json.encode(event);
        vertx.eventBus().publish(event.address(), event.toJson());
    }
}