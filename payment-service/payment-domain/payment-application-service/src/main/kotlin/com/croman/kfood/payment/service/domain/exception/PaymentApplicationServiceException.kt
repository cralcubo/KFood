package com.croman.kfood.payment.service.domain.exception

import com.croman.kfood.domain.exception.DomainException

class PaymentApplicationServiceException(override val message: String, override val cause: Throwable? = null):
    DomainException(message, cause)