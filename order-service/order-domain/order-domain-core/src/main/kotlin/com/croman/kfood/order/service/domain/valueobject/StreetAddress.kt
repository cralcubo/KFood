package com.croman.kfood.order.service.domain.valueobject

import java.util.UUID

/**
 * The ID in this class is only required to store a street in a DB.
 * This could deceive the nature of this class, because a value object should not really have an ID.
 * This might be a confusion from the teacher of the tutorial!
 */
data class StreetAddress(
    val id: UUID = UUID.randomUUID(),
    val street: String, val postalCode: String, val city: String
)