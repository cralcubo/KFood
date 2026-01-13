package com.croman.kfood.order.service.domain.outbox.scheduler.payment

import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PaymentOutboxCleanerScheduler(
    private val helper: PaymentOutboxHelper
): OutboxScheduler {
    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "@midnight")
    override fun processMessage() {
        helper.findByOutboxStatusAndSagaStatus(
            outboxStatus = OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED
        )
    }

}