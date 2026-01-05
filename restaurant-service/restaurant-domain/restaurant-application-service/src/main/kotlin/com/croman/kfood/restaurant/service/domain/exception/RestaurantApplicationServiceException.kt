package com.croman.kfood.restaurant.service.domain.exception

import com.croman.kfood.domain.exception.DomainException

class RestaurantApplicationServiceException(override val message: String, override val cause: Throwable? = null)
    : DomainException(message, cause)