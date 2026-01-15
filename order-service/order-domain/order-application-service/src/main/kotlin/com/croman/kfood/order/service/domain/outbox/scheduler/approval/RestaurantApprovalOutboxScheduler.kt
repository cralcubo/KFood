package com.croman.kfood.order.service.domain.outbox.scheduler.approval

import com.croman.kfood.order.service.domain.ports.output.message.publisher.approval.RestaurantApprovalRequestMessagePublisher
import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RestaurantApprovalOutboxScheduler(
    private val helper: ApprovalOutboxHelper,
    private val messagePublisher: RestaurantApprovalRequestMessagePublisher
) : OutboxScheduler {

    private val logger = KotlinLogging.logger {}


    @Transactional
    @Scheduled(
        fixedDelayString = $$"${order-service.outbox-scheduler-fixed-rate}",
        initialDelayString = $$"${order-service.outbox-scheduler-initial-delay}"
    )
    override fun processMessage() {
        val messages = helper.getMessages(
            outboxStatus = OutboxStatus.STARTED,
            SagaStatus.PROCESSING
        )

        logger.info {
            "Received ${messages.size} OrderApprovalOutboxMessages with IDs: ${messages.map { it.id }}." +
                    "Sending to message bus."
        }

        messages.forEach {
            messagePublisher.publish(it) { message, status ->
                helper.save(
                    message.copy(outboxStatus = status)
                )
                logger.info {"Saved updated OrderApprovalOutboxMessages with outbox status: $status " }
            }
        }

        logger.info {"${messages.size} OrderApprovalOutboxMessages sent to message bus."}

    }

}