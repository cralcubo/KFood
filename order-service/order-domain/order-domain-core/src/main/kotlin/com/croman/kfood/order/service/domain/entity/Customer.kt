package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.CustomerId

class Customer(id: CustomerId) : AggregateRoot<CustomerId>(id) {
}