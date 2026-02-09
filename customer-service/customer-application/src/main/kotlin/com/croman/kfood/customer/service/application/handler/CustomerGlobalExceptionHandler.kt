package com.croman.kfood.customer.service.application.handler

import com.croman.kfood.application.handler.ErrorDTO
import com.croman.kfood.application.handler.GlobalExceptionHandler
import com.croman.kfood.customer.service.domain.exception.CustomerDomainException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class CustomerGlobalExceptionHandler: GlobalExceptionHandler() {
    private val logger = KotlinLogging.logger {}

    @ResponseBody
    @ExceptionHandler(CustomerDomainException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: CustomerDomainException): ErrorDTO {
        logger.error(e) { "Error occurred while creating a customer" }
        return ErrorDTO(
            code = 400,
            message = e.message
        )
    }
}