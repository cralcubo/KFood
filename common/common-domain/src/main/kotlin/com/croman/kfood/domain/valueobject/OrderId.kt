package com.croman.kfood.domain.valueobject

import java.util.UUID

class OrderId(value: UUID) : BaseId<UUID>(value) {
    override fun toString() = "OrderId($value)"
}