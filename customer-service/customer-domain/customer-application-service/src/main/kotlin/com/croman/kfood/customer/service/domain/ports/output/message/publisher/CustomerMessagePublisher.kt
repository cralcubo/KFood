package com.croman.kfood.customer.service.domain.ports.output.message.publisher

import com.croman.kfood.customer.service.domain.event.CustomerCreatedEvent

interface CustomerMessagePublisher {

    fun publish(event: CustomerCreatedEvent)
}