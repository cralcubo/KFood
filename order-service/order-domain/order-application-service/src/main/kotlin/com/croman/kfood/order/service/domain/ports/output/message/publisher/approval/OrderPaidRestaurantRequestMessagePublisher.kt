package com.croman.kfood.order.service.domain.ports.output.message.publisher.approval

import com.croman.kfood.domain.event.publisher.DomainEventPublisher
import com.croman.kfood.order.service.domain.event.OrderEvent

interface OrderPaidRestaurantRequestMessagePublisher: DomainEventPublisher<OrderEvent.Paid> {
}