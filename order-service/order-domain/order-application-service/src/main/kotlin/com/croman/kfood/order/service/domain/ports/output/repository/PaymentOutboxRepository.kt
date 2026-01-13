package com.croman.kfood.order.service.domain.ports.output.repository

import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import java.util.UUID

interface PaymentOutboxRepository {

    fun save(message: OrderPaymentOutboxMessage): OrderPaymentOutboxMessage

    fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderPaymentOutboxMessage>

    fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ): List<OrderPaymentOutboxMessage>

    fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    )

}