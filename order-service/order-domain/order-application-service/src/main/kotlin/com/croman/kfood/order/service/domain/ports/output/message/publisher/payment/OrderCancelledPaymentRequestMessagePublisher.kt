package com.croman.kfood.order.service.domain.ports.output.message.publisher.payment

import com.croman.kfood.domain.event.publisher.DomainEventPublisher
import com.croman.kfood.order.service.domain.event.OrderEvent

interface OrderCancelledPaymentRequestMessagePublisher: DomainEventPublisher<OrderEvent.Cancelling> {
}