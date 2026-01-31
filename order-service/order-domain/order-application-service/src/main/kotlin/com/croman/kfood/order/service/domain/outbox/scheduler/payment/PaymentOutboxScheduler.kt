package com.croman.kfood.order.service.domain.outbox.scheduler.payment

import com.croman.kfood.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher
import com.croman.kfood.outbox.OutboxScheduler
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class PaymentOutboxScheduler(
    private val helper: PaymentOutboxHelper,
    private val messagePublisher: PaymentRequestMessagePublisher
) : OutboxScheduler {

    private val logger = KotlinLogging.logger {}

    @Transactional
    @Scheduled(
        fixedDelayString = $$"${order-service.outbox-scheduler-fixed-rate}",
        initialDelayString = $$"${order-service.outbox-scheduler-initial-delay}"
    )
    override fun processMessage() {

        // Query all the Outbox Messages concerned with Payment when the OutboxStatus is STARTED.
        // There are 2 type of events to be sent to the Payment-Service:
        // OrderCreated and OrderCancelled.
        //  - When an OrderCreated event is sent the Order is in PENDING status.
        //  - When an OrderCancelled event is sent, the Order is in CANCELLING status.
        // Those order status are translated to the SagaStatuses:
        // - STARTED
        // - COMPENSATING
        val messages = helper.getMessages(
            outboxStatus = OutboxStatus.STARTED,
            SagaStatus.STARTED, SagaStatus.COMPENSATING
        )

        if(messages.isNotEmpty()) {
            logger.info {
                "Received ${messages.size} OrderPaymentOutboxMessages with IDs: ${messages.map { it.id }}." +
                        "Sending to message bus."
            }

            messages.forEach {
                messagePublisher.publish(it) { message, status ->
                    helper.save(
                        message.copy(outboxStatus = status)
                    )
                    logger.info { "Saved updated OrderPaymentOutboxMessage with outbox status: $status " }
                }
            }

            logger.info { "${messages.size} OrderPaymentOutboxMessages sent to message bus." }
        }
    }
}