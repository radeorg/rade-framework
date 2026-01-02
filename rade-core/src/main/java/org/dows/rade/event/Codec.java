package org.dows.rade.event;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class Codec implements MessageCodec<DomainEvent, DomainEvent> {
    @Override
    public void encodeToWire(Buffer buffer, DomainEvent event) {
        buffer.appendBuffer(Json.encodeToBuffer(event));
    }

    @Override
    public DomainEvent decodeFromWire(int pos, Buffer buffer) {
        return DomainEvent.fromJson(buffer.toJsonObject().toString());
    }


    @Override
    public DomainEvent transform(DomainEvent event) {
        return event; // JVM 内可直接传引用
    }


    @Override
    public String name() {
        return "OrderCreatedEventCodec";
    }


    @Override
    public byte systemCodecID() {
        return -1;
    }
}