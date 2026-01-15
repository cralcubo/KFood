package com.croman.kfood.order.service.domain.outbox.model.payment

import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import java.time.ZonedDateTime
import java.util.UUID

data class OrderPaymentOutboxMessage (
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime? = null,
    val type: String,
    val payload: String,
    val sagaStatus: SagaStatus,
    val orderStatus: OrderStatus,
    val outboxStatus: OutboxStatus,
    val version: Int? = null,
)