package com.croman.kfood.payment.service.domain.port.output.repository.message.publisher

import com.croman.kfood.domain.event.publisher.DomainEventPublisher
import com.croman.kfood.payment.service.domain.event.PaymentEvent

interface PaymentFailedMessagePublisher: DomainEventPublisher<PaymentEvent.Failed>