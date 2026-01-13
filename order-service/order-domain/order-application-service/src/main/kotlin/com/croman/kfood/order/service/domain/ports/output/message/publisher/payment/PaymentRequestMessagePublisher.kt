package com.croman.kfood.order.service.domain.ports.output.message.publisher.payment

import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.croman.kfood.outbox.OutboxStatus
import java.util.function.BiConsumer

interface PaymentRequestMessagePublisher {
    fun publish(message: OrderPaymentOutboxMessage, outboxCallback: BiConsumer<OrderPaymentOutboxMessage, OutboxStatus>)
}