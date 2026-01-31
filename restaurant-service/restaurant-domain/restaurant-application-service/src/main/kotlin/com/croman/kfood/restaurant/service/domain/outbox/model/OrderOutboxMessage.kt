package com.croman.kfood.restaurant.service.domain.outbox.model

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
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
    val outboxStatus: OutboxStatus,
    val orderApprovalStatus: OrderApprovalStatus,
    val version: Int? = null,
)