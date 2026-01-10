package com.croman.kfood.domain.valueobject

import java.util.UUID

class RestaurantId(value: UUID) : BaseId<UUID>(value) {
    override fun toString() = "RestaurantId($value)"
}