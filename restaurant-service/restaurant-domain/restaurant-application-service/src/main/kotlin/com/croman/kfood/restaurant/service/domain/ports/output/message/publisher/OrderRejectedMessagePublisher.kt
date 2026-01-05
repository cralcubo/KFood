package com.croman.kfood.restaurant.service.domain.ports.output.message.publisher

import com.croman.kfood.domain.event.publisher.DomainEventPublisher
import com.croman.kfood.restaurant.service.domain.event.OrderApprovalEvent

interface OrderRejectedMessagePublisher: DomainEventPublisher<OrderApprovalEvent.Rejected> {
}