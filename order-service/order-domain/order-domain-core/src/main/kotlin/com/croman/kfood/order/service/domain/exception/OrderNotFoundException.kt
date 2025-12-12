package com.croman.kfood.order.service.domain.exception

import com.croman.kfood.domain.exception.DomainException

class OrderNotFoundException(override val message: String, override val cause: Throwable? = null)
    : DomainException(message, cause)