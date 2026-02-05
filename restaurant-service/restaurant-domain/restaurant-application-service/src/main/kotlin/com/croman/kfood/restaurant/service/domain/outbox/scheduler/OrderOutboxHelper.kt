package com.croman.kfood.restaurant.service.domain.outbox.scheduler

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.domain.exception.RestaurantDomainException
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderEventPayload
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderOutboxMessage
import com.croman.kfood.restaurant.service.domain.ports.output.repository.OrderOutboxRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

private const val ORDER_SAGA_NAME = "OrderProcessingSaga"

@Component
class OrderOutboxHelper(
    private val repository: OrderOutboxRepository,
    private val objectMapper: ObjectMapper,
) {

    @Transactional(readOnly = true)
    fun getCompletedOutboxMessage(sagaId: UUID, outboxStatus: OutboxStatus) =
        repository.getMessage(
            type = ORDER_SAGA_NAME,
            sagaId = sagaId,
            outboxStatus = outboxStatus,
        )

    @Transactional(readOnly = true)
    fun getMessages(outboxStatus: OutboxStatus) =
        repository.getMessages(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
        )

    @Transactional
    fun delete(outboxStatus: OutboxStatus) {
        repository.delete(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
        )
    }

    @Transactional
    fun save(
        payload: OrderEventPayload,
        approvalStatus: OrderApprovalStatus,
        outboxStatus: OutboxStatus,
        sagaId: UUID
    ) {
        val serializedPayload = runCatching {  objectMapper.writeValueAsString(payload) }
            .getOrElse { throw RestaurantDomainException("Could not serialize event payload", it) }

        repository.save(
            OrderOutboxMessage(
                id = UUID.randomUUID(),
                sagaId = sagaId,
                createdAt = payload.createdAt,
                processedAt = ZonedDateTime.now(ZoneId.of("UTC")),
                type = ORDER_SAGA_NAME,
                payload = serializedPayload,
                outboxStatus = outboxStatus,
                orderApprovalStatus = approvalStatus,
                version = null
            )
        )
    }

    @Transactional
    fun save(message: OrderOutboxMessage) =
        repository.save(message)

}