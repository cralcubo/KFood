package com.croman.kfood.application.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
open class GlobalExceptionHandler {
    private val logger = KotlinLogging.logger {}

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handle(exception: Exception): ErrorDTO {
        logger.error(exception){ "There is an error with the following message: ${exception.message}" }
        return ErrorDTO(code=500, message="Unexpected error",)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(exception: ValidationException): ErrorDTO {
        logger.error(exception){ "There was a validation error with the following message: ${exception.message}" }
        return ErrorDTO(code=400, message=exception.message ?: "Unknown validation error")
    }
}