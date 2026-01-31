package com.croman.kfood.order.service.domain.outbox.scheduler.approval

import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RestaurantApprovalOutboxCleanerScheduler(
    private val helper: ApprovalOutboxHelper
): OutboxScheduler {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "@midnight")
    override fun processMessage() {
        val messages = helper.getMessages(
            outboxStatus = OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED
        )
        if(messages.isNotEmpty()) {
            val payloads = messages.joinToString(separator = "\n") { it.payload }
            logger.info { "Received ${messages.size} OrderApprovalOutboxMessage to clean up. Payloads: $payloads" }

            helper.delete(
                outboxStatus = OutboxStatus.COMPLETED,
                SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED
            )
            logger.info { "${messages.size} OrderApprovalOutboxMessage cleaned up." }
        }
    }
}