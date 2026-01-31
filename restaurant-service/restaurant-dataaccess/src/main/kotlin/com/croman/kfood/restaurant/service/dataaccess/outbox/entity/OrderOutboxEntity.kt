package com.croman.kfood.restaurant.service.dataaccess.outbox.entity

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import com.croman.kfood.outbox.OutboxStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.ZonedDateTime
import java.util.UUID

@Table(name = "order_outbox")
@Entity
data class OrderOutboxEntity(
    @Id
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime?,
    val type: String,
    val payload: String,
    @Enumerated(EnumType.STRING)
    val outboxStatus: OutboxStatus,
    @Enumerated(EnumType.STRING)
    val approvalStatus: OrderApprovalStatus,
    @Version // Enable optimistic locking
    val version: Int?,
)