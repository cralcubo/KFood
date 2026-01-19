package com.croman.kfood.order.service.domain.ports.output.repository

import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import java.util.UUID

interface ApprovalOutboxRepository  {

    fun save(message: OrderApprovalOutboxMessage): OrderApprovalOutboxMessage

    fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderApprovalOutboxMessage>

    fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ): OrderApprovalOutboxMessage?

    fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    )
}