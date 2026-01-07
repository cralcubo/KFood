package com.croman.kfood.saga

import com.croman.kfood.domain.event.DomainEvent

/**
 * The Sage Step where:
 * T represents the Message in a queue of a Message Broker such as Kafka.
 * S represents the Event to be triggered if the step was successful.
 * U represent the Event to be triggered if the step was unsuccessful to proceed with a rollback operation.
 */
interface SagaStep<T, S: DomainEvent<*>, U: DomainEvent<*>> {
    fun processData(data: T): S
    fun rollback(data: T): U
}