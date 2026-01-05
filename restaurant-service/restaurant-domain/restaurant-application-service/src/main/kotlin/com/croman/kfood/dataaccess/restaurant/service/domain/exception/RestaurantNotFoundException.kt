package com.croman.kfood.dataaccess.restaurant.service.domain.exception

import com.croman.kfood.domain.exception.DomainException

class RestaurantNotFoundException(override val message: String, override val cause: Throwable? = null)
    : DomainException(message, cause)