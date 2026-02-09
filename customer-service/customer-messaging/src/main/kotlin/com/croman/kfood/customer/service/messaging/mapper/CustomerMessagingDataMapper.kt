package com.croman.kfood.customer.service.messaging.mapper

import com.croman.kfood.customer.service.domain.event.CustomerCreatedEvent
import com.croman.kfood.kafka.order.avro.model.CustomerAvroModel
import org.springframework.stereotype.Component

@Component
class CustomerMessagingDataMapper {

    fun CustomerCreatedEvent.toAvroModel(): CustomerAvroModel =
        CustomerAvroModel.newBuilder()
            .setId(customer.id.value)
            .setUsername(customer.username)
            .setFirstName(customer.firstName)
            .setLastName(customer.lastName)
            .build()
}