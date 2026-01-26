package com.croman.kfood.payment.service.domain.outbox.scheduler

import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderOutboxCleanerScheduler(
    private val helper: OrderOutboxHelper
): OutboxScheduler {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "@midnight")
    @Transactional
    override fun processMessage() {
        val completedMessages = helper.getMessages(OutboxStatus.COMPLETED)

        if (completedMessages.isNotEmpty()) {
            logger.info { "${completedMessages.size} COMPLETED OrderOutboxMessages to be cleaned from the outbox database!" }
            helper.delete(OutboxStatus.COMPLETED)
            logger.info { "${completedMessages.size} deleted OrderOutboxMessages." }
        }

    }
}