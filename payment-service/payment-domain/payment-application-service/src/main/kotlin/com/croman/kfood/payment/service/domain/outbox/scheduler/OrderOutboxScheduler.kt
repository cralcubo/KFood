package com.croman.kfood.payment.service.domain.outbox.scheduler

import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.domain.port.output.message.publisher.PaymentResponseMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderOutboxScheduler(
    private val helper: OrderOutboxHelper,
    private val publisher: PaymentResponseMessagePublisher
) : OutboxScheduler {

    private val logger = KotlinLogging.logger {}

    @Scheduled(
        fixedDelayString = $$"${payment-service.outbox-scheduler-fixed-rate}",
        initialDelayString = $$"${payment-service.outbox-scheduler-initial-delay}"
    )

    @Transactional
    override fun processMessage() {
        val messages = helper.getMessages(OutboxStatus.STARTED)
        if(messages.isNotEmpty()) {
            logger.info { "Retrieved ${messages.size} STARTED OrderOutboxMessage from the outbox table. Sending it to kafka!" }
            messages.forEach {
                publisher.publish(
                    message = it,
                    updateMessageCallback = { message, status ->
                        val updatedMessage = message.copy(outboxStatus = status)
                        helper.save(updatedMessage)
                    }
                )
            }
            logger.info { "${messages.size} OrderOutboxMessages sent to kafka" }
        }
    }

}