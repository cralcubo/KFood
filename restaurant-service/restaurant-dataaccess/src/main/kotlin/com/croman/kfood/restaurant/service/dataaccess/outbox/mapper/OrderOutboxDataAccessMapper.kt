package com.croman.kfood.restaurant.service.dataaccess.outbox.mapper

import com.croman.kfood.restaurant.service.dataaccess.outbox.entity.OrderOutboxEntity
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderOutboxMessage
import org.springframework.stereotype.Component

@Component
class OrderOutboxDataAccessMapper {

    fun OrderOutboxMessage.toEntity() =
        OrderOutboxEntity(
            id = id,
            sagaId = sagaId,
            createdAt = createdAt,
            processedAt = processedAt,
            type = type,
            payload = payload,
            outboxStatus = outboxStatus,
            approvalStatus = orderApprovalStatus,
            version = version
        )

    fun OrderOutboxEntity.toMessage() =
        OrderOutboxMessage(
            id = id,
            sagaId = sagaId,
            createdAt = createdAt,
            processedAt = processedAt,
            type = type,
            payload = payload,
            outboxStatus = outboxStatus,
            orderApprovalStatus = approvalStatus,
            version = version
        )
}