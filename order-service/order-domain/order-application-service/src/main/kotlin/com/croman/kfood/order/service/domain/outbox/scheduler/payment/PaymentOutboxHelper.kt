package com.croman.kfood.order.service.domain.outbox.scheduler.payment

import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.croman.kfood.order.service.domain.ports.output.repository.PaymentOutboxRepository
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private const val ORDER_SAGA_NAME = "OrderProcessingSaga"

@Component
class PaymentOutboxHelper(
    private val repository: PaymentOutboxRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true) // readOnly: This won't change the state and will only get data
    fun findByOutboxStatusAndSagaStatus(outboxStatus: OutboxStatus, vararg sagaStatus: SagaStatus) =
        repository.findByTypeAndOutboxStatusAndSagaStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus, *sagaStatus
        )

    @Transactional(readOnly = true)
    fun findBySagaIdAndSagaStatus(sagaId: UUID, vararg sagaStatus: SagaStatus) =
        repository.findByTypeAndSagaIdAndSagaStatus(
            type = ORDER_SAGA_NAME,
            sagaId, *sagaStatus
        )

    @Transactional
    fun save(message: OrderPaymentOutboxMessage) {
        repository.save(message)
        logger.info { "OrderPaymentOutboxMessage saved with id ${message.id}" }
    }
}