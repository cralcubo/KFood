package com.croman.kfood.dataaccess.restaurant.service.domain.ports.output.message.publisher

import com.croman.kfood.domain.event.publisher.DomainEventPublisher
import com.croman.kfood.event.OrderApprovalEvent

interface OrderApprovedMessagePublisher: DomainEventPublisher<OrderApprovalEvent.Approved> {
}