package com.croman.kfood.restaurant.service.dataaccess.outbox.adapter

import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper
import com.croman.kfood.restaurant.service.dataaccess.outbox.repository.OrderOutboxJpaRepository
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderOutboxMessage
import com.croman.kfood.restaurant.service.domain.ports.output.repository.OrderOutboxRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrderOutboxRepositoryImpl(
    private val jpaRepository: OrderOutboxJpaRepository,
    private val mapper: OrderOutboxDataAccessMapper
) : OrderOutboxRepository {
    override fun save(message: OrderOutboxMessage) = with(mapper) {
        jpaRepository.save(message.toEntity())
            .toMessage()
    }

    override fun getMessages(
        type: String,
        outboxStatus: OutboxStatus
    ): List<OrderOutboxMessage> = with(mapper) {
        jpaRepository.findByTypeAndOutboxStatus(type, outboxStatus)
            ?.map { it.toMessage() }
            ?: emptyList()
    }

    override fun getMessage(
        type: String,
        sagaId: UUID,
        outboxStatus: OutboxStatus
    ): OrderOutboxMessage?  = with(mapper) {
        jpaRepository.findByTypeAndSagaIdAndOutboxStatus(type = type, outboxStatus = outboxStatus, sagaId = sagaId)
        ?.toMessage()
    }

    override fun delete(type: String, outboxStatus: OutboxStatus) {
        jpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus)
    }
}