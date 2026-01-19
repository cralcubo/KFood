package com.croman.kfood.order.service.dataaccess.outbox.approval.repository

import com.croman.kfood.order.service.dataaccess.outbox.approval.entity.ApprovalOutboxEntity
import com.croman.kfood.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ApprovalOutboxJpaRepository: JpaRepository<ApprovalOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: Set<SagaStatus>
    ): List<ApprovalOutboxEntity>?

    fun findByTypeAndSagaIdAndSagaStatusIn(
        type: String,
        sagaId: UUID,
        sagaStatus: Set<SagaStatus>
    ): ApprovalOutboxEntity?

    fun deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type:String,
        outboxStatus: OutboxStatus,
        sagaStatus: Set<SagaStatus>
    )
}