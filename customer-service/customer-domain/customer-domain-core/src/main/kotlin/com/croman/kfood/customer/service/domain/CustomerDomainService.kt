package com.croman.kfood.customer.service.domain

import com.croman.kfood.customer.service.domain.entity.Customer
import com.croman.kfood.customer.service.domain.event.CustomerCreatedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.ZoneOffset
import java.time.ZonedDateTime

interface CustomerDomainService {
    fun validateAndInitializeCustomer(customer: Customer) : CustomerCreatedEvent
}

class CustomerDomainServiceImpl : CustomerDomainService {
    private val logger = KotlinLogging.logger {}

    override fun validateAndInitializeCustomer(customer: Customer): CustomerCreatedEvent {
        logger.info { "Initializing customer $customer.id" }
        return CustomerCreatedEvent(customer, ZonedDateTime.now(ZoneOffset.UTC))
    }

}
