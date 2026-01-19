package com.croman.kfood.order.service.dataaccess.outbox.payment.entity

import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID

@Table(name = "payment_outbox")
@Entity
data class PaymentOutboxEntity(
    @Id
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime?,
    val type: String, // This is the type set in the class PaymentOutboxHelper
    val payload: String,
    @Enumerated(EnumType.STRING)
    val sagaStatus: SagaStatus,
    @Enumerated(EnumType.STRING)
    val outboxStatus: OutboxStatus,
    @Enumerated(EnumType.STRING)
    val orderStatus: OrderStatus,
    @Version // Enable optimistic locking
    val version: Int?
)