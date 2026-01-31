package com.croman.kfood.restaurant.service.dataaccess.outbox.repository

import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.dataaccess.outbox.entity.OrderOutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderOutboxJpaRepository : JpaRepository<OrderOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus): List<OrderOutboxEntity>?

    fun findByTypeAndSagaIdAndOutboxStatus(type: String, sagaId: UUID, outboxStatus: OutboxStatus): OrderOutboxEntity?

    fun deleteByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus)
}