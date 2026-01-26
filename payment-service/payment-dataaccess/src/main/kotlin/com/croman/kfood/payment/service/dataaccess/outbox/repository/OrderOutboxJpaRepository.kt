package com.croman.kfood.payment.service.dataaccess.outbox.repository

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.dataaccess.outbox.entity.OrderOutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderOutboxJpaRepository : JpaRepository<OrderOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus): List<OrderOutboxEntity>

    fun findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus,
    ): OrderOutboxEntity?

    fun deleteByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus)
}