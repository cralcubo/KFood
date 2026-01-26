package com.croman.kfood.payment.service.domain.port.output.message.publisher

import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.domain.outbox.model.OrderOutboxMessage
import org.apache.logging.log4j.util.BiConsumer

interface PaymentResponseMessagePublisher {
    fun publish(message: OrderOutboxMessage, updateMessageCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>)
}