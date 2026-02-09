package com.croman.kfood.customer.service.domain

import com.croman.kfood.customer.service.domain.create.CreateCustomerCommand
import com.croman.kfood.customer.service.domain.create.CreateCustomerResponse
import com.croman.kfood.customer.service.domain.event.CustomerCreatedEvent
import com.croman.kfood.customer.service.domain.mapper.CustomerDataMapper
import com.croman.kfood.customer.service.domain.ports.input.service.CustomerApplicationService
import com.croman.kfood.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher
import com.croman.kfood.customer.service.domain.ports.output.repository.CustomerRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class CustomerApplicationServiceImpl(
    private val dataMapper: CustomerDataMapper,
    private val customerDomainService: CustomerDomainService,
    private val repository: CustomerRepository,
    private val messagePublisher: CustomerMessagePublisher
) : CustomerApplicationService {
    private val logger = KotlinLogging.logger {}

    override fun createCustomer(customerCommand: CreateCustomerCommand): CreateCustomerResponse {
        val customerCreatedEvent = create(customerCommand)
        messagePublisher.publish(customerCreatedEvent)

        return with(dataMapper) {
            customerCreatedEvent.customer.toResponse("Customer ${customerCommand.username} successfully created!")
        }
    }

    @Transactional
    private fun create(command: CreateCustomerCommand): CustomerCreatedEvent {
        val customer = with(dataMapper) { command.toNewCustomer() }
        val event = customerDomainService.validateAndInitializeCustomer(customer)
        repository.saveCustomer(customer)
        logger.info { "Customer ${customer.id} created" }
        return event
    }
}