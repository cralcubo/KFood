package com.croman.kfood.order.service.domain.outbox.scheduler.payment

import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.order.service.domain.exception.OrderDomainException
import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.croman.kfood.order.service.domain.ports.output.repository.PaymentOutboxRepository
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private const val ORDER_SAGA_NAME = "OrderProcessingSaga"

@Component
class PaymentOutboxHelper(
    private val repository: PaymentOutboxRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true) // readOnly: This won't change the state and will only get data
    fun getMessages(outboxStatus: OutboxStatus, vararg sagaStatus: SagaStatus) =
        repository.findByTypeAndOutboxStatusAndSagaStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus, *sagaStatus
        )

    @Transactional(readOnly = true)
    fun getMessage(sagaId: UUID, vararg sagaStatus: SagaStatus) =
        repository.findByTypeAndSagaIdAndSagaStatus(
            type = ORDER_SAGA_NAME,
            sagaId, *sagaStatus
        )

    @Transactional
    fun save(message: OrderPaymentOutboxMessage) {
        repository.save(message)
        logger.info { "OrderPaymentOutboxMessage saved with id ${message.id}" }
    }

    @Transactional
    fun save(
        payload: OrderPaymentEventPayload,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
        outboxStatus: OutboxStatus,
        sagaId: UUID
    ) {
        val serializedPayload = runCatching {
            objectMapper.writeValueAsString(payload)
        }.getOrElse {
            throw OrderDomainException("There was an error serializing OrderPaymentEventPayload for order ${payload.orderId}")
        }
        val message = OrderPaymentOutboxMessage(
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

    @Transactional
    fun delete(outboxStatus: OutboxStatus, vararg sagaStatus: SagaStatus) {
        repository.deleteByTypeAndOutboxStatusAndSagaStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
            sagaStatus = sagaStatus
        )
    }
}