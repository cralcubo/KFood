package com.croman.kfood.customer.service.domain.exception

import com.croman.kfood.domain.exception.DomainException

class CustomerDomainException(override val message: String, cause: Throwable? = null) : DomainException(message, cause)