package com.croman.kfood.customer.service.domain.event

import com.croman.kfood.customer.service.domain.entity.Customer
import com.croman.kfood.domain.event.DomainEvent
import java.time.LocalDateTime
import java.time.ZonedDateTime

class CustomerCreatedEvent(
    val customer: Customer,
    val createdAt: ZonedDateTime,
) : DomainEvent<Customer> {
}