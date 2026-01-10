package com.croman.kfood.payment.service.domain.port.output.message.publisher

import com.croman.kfood.domain.event.publisher.DomainEventPublisher
import com.croman.kfood.payment.service.domain.event.PaymentEvent

interface PaymentCompletedMessagePublisher: DomainEventPublisher<PaymentEvent.Completed>