package com.croman.kfood.customer.service.domain.create

import java.util.UUID

data class CreateCustomerResponse(
    val customerId: UUID,
    val message: String
)