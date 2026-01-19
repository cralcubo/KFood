package com.croman.kfood.order.service.dataaccess.outbox.approval.mapper

import com.croman.kfood.order.service.dataaccess.outbox.approval.entity.ApprovalOutboxEntity
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import org.springframework.stereotype.Component

@Component
class ApprovalOutboxDataAccessMapper {

    fun OrderApprovalOutboxMessage.toEntity() =
        ApprovalOutboxEntity(
            id = id,
            sagaId = sagaId,
            createdAt = createdAt,
            processedAt = processedAt,
            type = type,
            payload = payload,
            sagaStatus = sagaStatus,
            outboxStatus = outboxStatus,
            orderStatus = orderStatus,
            version = version
        )

    fun ApprovalOutboxEntity.toMessage() =
        OrderApprovalOutboxMessage(
            id = id,
            sagaId = sagaId,
            createdAt = createdAt,
            processedAt = processedAt,
            type = type,
            payload = payload,
            sagaStatus = sagaStatus,
            outboxStatus = outboxStatus,
            orderStatus = orderStatus,
            version = version
        )

}