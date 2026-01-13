package com.croman.kfood.saga

enum class SagaStatus {
    STARTED, PROCESSING, SUCCEEDED, COMPENSATING, COMPENSATED, FAILED
}