package com.NTFOODS.Api_tanty.shared.kernel.event;

public interface EventPublisher {
    void publish(DomainEvent event);
    void publishAll(Iterable<DomainEvent> events);
}
