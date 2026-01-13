package com.croman.kfood.order.service.domain.ports.output.message.publisher.approval

import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.croman.kfood.outbox.OutboxStatus
import java.util.function.BiConsumer

interface RestaurantApprovalRequestMessagePublisher {

    fun publish(
        message: OrderApprovalOutboxMessage,
        outboxCallback: BiConsumer<OrderApprovalOutboxMessage, OutboxStatus>
    )
}