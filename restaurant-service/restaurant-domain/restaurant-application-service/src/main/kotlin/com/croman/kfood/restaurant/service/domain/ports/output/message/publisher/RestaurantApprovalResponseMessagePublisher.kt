package com.croman.kfood.restaurant.service.domain.ports.output.message.publisher

import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderOutboxMessage
import org.apache.logging.log4j.util.BiConsumer

interface RestaurantApprovalResponseMessagePublisher {
    fun publish(message: OrderOutboxMessage, outboxCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>)
}