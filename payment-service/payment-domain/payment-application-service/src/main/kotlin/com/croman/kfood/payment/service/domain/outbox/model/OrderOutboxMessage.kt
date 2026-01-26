package com.croman.kfood.payment.service.domain.outbox.model

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import java.time.ZonedDateTime
import java.util.UUID

data class OrderOutboxMessage(
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime? = null,
    val type: String,
    val payload: String,
    val paymentStatus: PaymentStatus,
    val outboxStatus: OutboxStatus,
    val version: Int? = null,
)