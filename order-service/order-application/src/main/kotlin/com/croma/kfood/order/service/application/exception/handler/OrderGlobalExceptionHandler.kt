package com.croma.kfood.order.service.application.exception.handler

import com.croman.kfood.application.handler.ErrorDTO
import com.croman.kfood.application.handler.GlobalExceptionHandler
import com.croman.kfood.order.service.domain.exception.OrderDomainException
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class OrderGlobalExceptionHandler : GlobalExceptionHandler() {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(exception: OrderDomainException): ErrorDTO {
        logger.error(exception) { "Error occurred while processing request" }
        return ErrorDTO(code = 400, message = exception.message)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(exception: OrderNotFoundException): ErrorDTO {
        logger.error(exception) { "Order not found" }
        return ErrorDTO(code = 404, message = exception.message)
    }


}