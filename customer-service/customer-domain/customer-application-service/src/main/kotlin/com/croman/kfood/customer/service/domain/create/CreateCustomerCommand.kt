package com.croman.kfood.customer.service.domain.create

import jakarta.validation.constraints.NotNull

data class CreateCustomerCommand(
    @param:NotNull
    val username: String,
    @param:NotNull
    val firstName: String,
    @param:NotNull
    val lastName: String,
)