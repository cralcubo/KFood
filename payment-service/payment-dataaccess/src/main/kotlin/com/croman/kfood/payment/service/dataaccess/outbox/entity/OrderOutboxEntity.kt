package com.croman.kfood.payment.service.dataaccess.outbox.entity

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Table(name = "order_outbox")
@Entity
data class OrderOutboxEntity(
    @Id
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime?,
    val type: String, // This is the type set in the class PaymentOutboxHelper
    val payload: String,
    @Enumerated(EnumType.STRING)
    val outboxStatus: OutboxStatus,
    @Enumerated(EnumType.STRING)
    val paymentStatus: PaymentStatus,
    @Version // Enable optimistic locking
    val version: Int?
)