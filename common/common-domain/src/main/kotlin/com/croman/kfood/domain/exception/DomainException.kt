package com.croman.kfood.domain.exception

import java.lang.RuntimeException

open class DomainException(override val message: String?, override val cause: Throwable?) : RuntimeException(message, cause)