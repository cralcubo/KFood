package com.croman.kfood.payment.service.dataaccess.outbox.adapter

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper
import com.croman.kfood.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository
import com.croman.kfood.payment.service.domain.outbox.model.OrderOutboxMessage
import com.croman.kfood.payment.service.domain.port.output.repository.OrderOutboxRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrderOutboxRepositoryImpl(
    private val jpaRepository: OrderOutboxJpaRepository,
    private val dataAccessMapper: OrderOutboxDataAccessMapper
) : OrderOutboxRepository {
    override fun save(message: OrderOutboxMessage): OrderOutboxMessage = with(dataAccessMapper) {
        jpaRepository.save(message.toEntity()).toMessage()
    }

    override fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus
    ): List<OrderOutboxMessage> = with(dataAccessMapper) {
        jpaRepository.findByTypeAndOutboxStatus(type, outboxStatus)
            .map { it.toMessage() }
    }

    override fun findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus
    ): OrderOutboxMessage? = with(dataAccessMapper) {
        jpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            type, sagaId, paymentStatus, outboxStatus
        )?.toMessage()
    }

    override fun deleteByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus) {
        jpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus)
    }
}