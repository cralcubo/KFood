package com.croman.kfood.payment.service.domain.exception

import com.croman.kfood.domain.exception.DomainException


sealed class CreditException(override val message: String, override val cause: Throwable? = null):
    DomainException(message, cause) {

    class Insufficient(override val message: String, override val cause: Throwable? = null): CreditException(message, cause)
    class Invalid(override val message: String, override val cause: Throwable? = null): CreditException(message, cause)
}
