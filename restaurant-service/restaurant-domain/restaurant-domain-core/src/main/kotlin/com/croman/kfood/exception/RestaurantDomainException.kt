package com.croman.kfood.exception

import com.croman.kfood.domain.exception.DomainException

class RestaurantDomainException(override val message: String, override val cause: Throwable? = null)
    : DomainException(message, cause)