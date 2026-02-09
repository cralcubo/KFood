package com.croman.kfood.customer.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.CustomerId

class Customer(
    val id: CustomerId,
    val username: String,
    val firstName: String,
    val lastName: String
) : AggregateRoot<CustomerId>(id)