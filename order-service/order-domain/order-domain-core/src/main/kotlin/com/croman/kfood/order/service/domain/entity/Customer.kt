package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.CustomerId
import java.util.UUID

class Customer(val id: CustomerId) : AggregateRoot<CustomerId>(id) {

    companion object {

        fun instantiate(id: CustomerId) =
            Customer(id)

        fun create() =
            instantiate(CustomerId(UUID.randomUUID()))
    }
}