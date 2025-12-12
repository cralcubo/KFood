package com.croman.kfood.domain.valueobject

import java.util.UUID

/**
 * The OrderId will be used by all micro-services in KFood
 */
class OrderId(value: UUID) : BaseId<UUID>(value)