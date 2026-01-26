package com.croman.kfood.payment.service.domain.outbox.scheduler

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.domain.exception.PaymentDomainException
import com.croman.kfood.payment.service.domain.outbox.model.OrderEventPayload
import com.croman.kfood.payment.service.domain.outbox.model.OrderOutboxMessage
import com.croman.kfood.payment.service.domain.port.output.repository.OrderOutboxRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

private const val ORDER_SAGA_NAME = "OrderProcessingSaga"

@Component
class OrderOutboxHelper(
    private val repository: OrderOutboxRepository,
    private val objectMapper: ObjectMapper,
) {

    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun getCompletedMessage(sagaId: UUID, paymentStatus: PaymentStatus): OrderOutboxMessage? =
        repository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            type = ORDER_SAGA_NAME,
            sagaId = sagaId,
            paymentStatus = paymentStatus,
            outboxStatus = OutboxStatus.COMPLETED
        )

    @Transactional(readOnly = true)
    fun getMessages(outboxStatus: OutboxStatus) =
        repository.findByTypeAndOutboxStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus
        )

    @Transactional
    fun delete(outboxStatus: OutboxStatus) {
        repository.deleteByTypeAndOutboxStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus
        )
    }

    @Transactional
    fun save(message: OrderOutboxMessage) =
        repository.save(message)

    @Transactional
    fun save(
        payload: OrderEventPayload,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus,
        sagaId: UUID,
    ): OrderOutboxMessage {
        val serializedPayload = runCatching {  objectMapper.writeValueAsString(payload) }
            .getOrElse { throw PaymentDomainException("There was not possible to serialize $payload") }
        val message = OrderOutboxMessage(
            id = UUID.randomUUID(),
            sagaId = sagaId,
            createdAt = payload.createdAt,
            processedAt = ZonedDateTime.now(ZoneOffset.UTC),
            type = ORDER_SAGA_NAME,
            payload = serializedPayload,
            paymentStatus = paymentStatus,
            outboxStatus = outboxStatus,
        )

        return repository.save(message)
    }

}