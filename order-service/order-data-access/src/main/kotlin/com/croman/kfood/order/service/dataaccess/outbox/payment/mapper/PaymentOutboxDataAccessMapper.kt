package com.croman.kfood.order.service.dataaccess.outbox.payment.mapper

import com.croman.kfood.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import org.springframework.stereotype.Component

@Component
class PaymentOutboxDataAccessMapper {

    fun OrderPaymentOutboxMessage.toEntity() =
        PaymentOutboxEntity(
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

    fun PaymentOutboxEntity.toMessage() =
        OrderPaymentOutboxMessage(
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