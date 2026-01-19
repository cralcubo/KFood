package com.croman.kfood.order.service.dataaccess.outbox.payment.repository

import com.croman.kfood.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentOutboxJpaRepository : JpaRepository<PaymentOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: Set<SagaStatus>
    ): List<PaymentOutboxEntity>?

    fun findByTypeAndSagaIdAndSagaStatusIn(
        type: String,
        sagaId: UUID,
        sagaStatus: Set<SagaStatus>
    ): PaymentOutboxEntity?

    fun deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type:String,
        outboxStatus: OutboxStatus,
        sagaStatus: Set<SagaStatus>
    )
}