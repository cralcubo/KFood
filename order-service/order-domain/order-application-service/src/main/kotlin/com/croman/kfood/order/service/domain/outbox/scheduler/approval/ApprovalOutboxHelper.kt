package com.croman.kfood.order.service.domain.outbox.scheduler.approval

import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.order.service.domain.exception.OrderDomainException
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.croman.kfood.order.service.domain.ports.output.repository.ApprovalOutboxRepository
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private const val ORDER_SAGA_NAME = "OrderProcessingSaga"

@Component
class ApprovalOutboxHelper(
    private val repository: ApprovalOutboxRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional(readOnly = true)
    fun getMessages(outboxStatus: OutboxStatus, vararg sagaStatus: SagaStatus) =
        repository.findByTypeAndOutboxStatusAndSagaStatus(
            type = ORDER_SAGA_NAME ,
            outboxStatus = outboxStatus,
            sagaStatus = sagaStatus
        )

    fun getMessage(sagaId: UUID, vararg sagaStatus: SagaStatus) =
        repository.findByTypeAndSagaIdAndSagaStatus(
            type = ORDER_SAGA_NAME,
            sagaId = sagaId,
            sagaStatus = sagaStatus
        )

    @Transactional
    fun save(message: OrderApprovalOutboxMessage) =
        repository.save(message)

    fun delete(outboxStatus: OutboxStatus, vararg sagaStatus: SagaStatus) {
        repository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, *sagaStatus)
    }

    @Transactional
    fun save(
        payload: OrderApprovalEventPayload,
        outboxStatus: OutboxStatus,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
        sagaId: UUID
    ) {
        val serializedPayload = runCatching {
            objectMapper.writeValueAsString(payload)
        }.getOrElse {
            throw OrderDomainException("There was an error serializing OrderApprovalEventPayload for order ${payload.orderId}")
        }
        val message = OrderApprovalOutboxMessage(
            id = UUID.randomUUID(),
            sagaId = sagaId,
            createdAt = payload.createdAt,
            type = ORDER_SAGA_NAME,
            payload = serializedPayload,
            sagaStatus = sagaStatus,
            orderStatus = orderStatus,
            outboxStatus = outboxStatus,
        )
        repository.save(message)
    }


}