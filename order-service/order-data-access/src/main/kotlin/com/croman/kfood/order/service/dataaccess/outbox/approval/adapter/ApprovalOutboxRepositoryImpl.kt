package com.croman.kfood.order.service.dataaccess.outbox.approval.adapter

import com.croman.kfood.order.service.dataaccess.outbox.approval.mapper.ApprovalOutboxDataAccessMapper
import com.croman.kfood.order.service.dataaccess.outbox.approval.repository.ApprovalOutboxJpaRepository
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.croman.kfood.order.service.domain.ports.output.repository.ApprovalOutboxRepository
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ApprovalOutboxRepositoryImpl(
    val jpaRepository: ApprovalOutboxJpaRepository,
    val mapper: ApprovalOutboxDataAccessMapper
) : ApprovalOutboxRepository {

    override fun save(message: OrderApprovalOutboxMessage): OrderApprovalOutboxMessage =
        with(mapper) {
            jpaRepository.save(message.toEntity())
                .toMessage()
        }

    override fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderApprovalOutboxMessage> =
        with(mapper) {
            jpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, sagaStatus.toSet())
                ?.map { it.toMessage() }
                ?: emptyList()
        }

    override fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ): OrderApprovalOutboxMessage? =
        with(mapper) {
            jpaRepository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, sagaStatus.toSet())
                ?.toMessage()
        }


    override fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) {
        jpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus, sagaStatus.toSet())
    }
}