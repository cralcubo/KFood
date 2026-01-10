package com.croman.kfood.domain.valueobject

import java.util.UUID

class CustomerId(value: UUID) : BaseId<UUID>(value) {
    override fun toString() = "CustomerId($value)"
}