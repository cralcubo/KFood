package com.croman.kfood.order.service.dataaccess.outbox.payment.adapter

import com.croman.kfood.order.service.dataaccess.outbox.payment.mapper.PaymentOutboxDataAccessMapper
import com.croman.kfood.order.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository
import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.croman.kfood.order.service.domain.ports.output.repository.PaymentOutboxRepository
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PaymentOutboxRepositoryImpl(
    private val repository: PaymentOutboxJpaRepository,
    private val mapper: PaymentOutboxDataAccessMapper
) : PaymentOutboxRepository {

    override fun save(message: OrderPaymentOutboxMessage) =
        with(mapper) {
            repository.save(message.toEntity())
                .toMessage()
        }

    override fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderPaymentOutboxMessage> =
        with(mapper) {
            repository.findByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, sagaStatus.toSet())
                ?.map { it.toMessage() }
                ?: emptyList()
        }

    override fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ): OrderPaymentOutboxMessage? =
        with(mapper) {
            repository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, sagaStatus.toSet())
                ?.toMessage()
        }

    override fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) {
        repository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, sagaStatus.toSet())
    }
}