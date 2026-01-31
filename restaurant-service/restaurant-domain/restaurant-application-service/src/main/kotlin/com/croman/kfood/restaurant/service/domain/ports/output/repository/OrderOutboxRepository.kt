package com.croman.kfood.restaurant.service.domain.ports.output.repository

import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderOutboxMessage
import java.util.UUID

interface OrderOutboxRepository {

    fun save(message: OrderOutboxMessage): OrderOutboxMessage

    fun getMessages(type: String, outboxStatus: OutboxStatus): List<OrderOutboxMessage>

    fun getMessage(type: String, sagaId: UUID, outboxStatus: OutboxStatus): OrderOutboxMessage?

    fun delete(type: String, outboxStatus: OutboxStatus)
}