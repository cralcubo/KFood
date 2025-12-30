package com.croman.kfood.order.service.domain.valueobject

import java.util.UUID

/**
 * The ID in this class is only required to store a street in a DB.
 * This could deceive the nature of this class, because a value object should not really have an ID.
 * This causes confusion from the teacher of the tutorial!
 */
class StreetAddress private constructor(
    val id: UUID,
    val street: String, val postalCode: String, val city: String
) {
    companion object {
        fun of(street: String, postalCode: String, city: String) =
            instantiate(UUID.randomUUID(), street, postalCode, city)

        fun instantiate(id: UUID, street: String, postalCode: String, city: String) =
            StreetAddress(id, street, postalCode, city)

    }
}