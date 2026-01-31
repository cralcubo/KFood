package com.croman.kfood.restaurant.service.domain.outbox.scheduler

import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderOutboxCleanerScheduler(
    private val helper: OrderOutboxHelper
) : OutboxScheduler {

    private val logger = KotlinLogging.logger {}

    @Transactional
    @Scheduled(cron = "@midnight")
    override fun processMessage() {
        val completedMessages = helper.getMessages(OutboxStatus.COMPLETED)
        if(completedMessages.isNotEmpty()) {
            logger.info { "Deleting ${completedMessages.size} outbox messages" }
            helper.delete(OutboxStatus.COMPLETED)
            logger.info { "Deleted ${completedMessages.size} outbox messages" }
        }
    }
}