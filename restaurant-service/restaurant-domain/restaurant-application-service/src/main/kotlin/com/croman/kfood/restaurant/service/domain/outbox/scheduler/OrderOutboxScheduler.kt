package com.croman.kfood.restaurant.service.domain.outbox.scheduler

import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderOutboxScheduler(
    private val helper: OrderOutboxHelper,
    private val messagePublisher: RestaurantApprovalResponseMessagePublisher
) : OutboxScheduler {
    private val logger = KotlinLogging.logger {}

    @Transactional
    @Scheduled(
        fixedDelayString = $$"${restaurant-service.outbox-scheduler-fixed-rate}",
        initialDelayString = $$"${restaurant-service.outbox-scheduler-initial-delay}"
    )
    override fun processMessage() {
        val messages = helper.getMessages(OutboxStatus.STARTED)
        if(messages.isNotEmpty()) {
            logger.info { "Received ${messages.size} OrderOutboxMessages. Sending it to the message bus." }
            messages.forEach {
                messagePublisher.publish(it) { message, status ->
                    helper.save(
                        message.copy(outboxStatus = status)
                    )
                }
            }
            logger.info { "${messages.size} OrderOutboxMessages sent to the message bus.  " }
        }
    }
}