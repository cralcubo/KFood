package com.croman.kfood.order.service.dataaccess.outbox.approval.entity

import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "restaurant_approval_outbox")
data class ApprovalOutboxEntity(
    @Id
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime?,
    val type: String, // This is the type set in the class ApprovalOutboxHelper
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