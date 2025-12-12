package com.croman.kfood.domain.event.publisher

import com.croman.kfood.domain.event.DomainEvent

interface DomainEventPublisher<T: DomainEvent<*>> {

    fun publish(event: T)

}