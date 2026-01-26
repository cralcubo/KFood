package com.croman.kfood.payment.service.domain.port.output.repository

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.domain.outbox.model.OrderOutboxMessage
import java.util.UUID

interface OrderOutboxRepository {
    fun save(message: OrderOutboxMessage): OrderOutboxMessage

    fun findByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus): List<OrderOutboxMessage>

    fun findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus
    ): OrderOutboxMessage?

    fun deleteByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus)

}