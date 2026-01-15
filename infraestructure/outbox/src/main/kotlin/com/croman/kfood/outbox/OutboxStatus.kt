package com.croman.kfood.outbox

enum class OutboxStatus {
    STARTED, COMPLETED,
    // The failed status will be the result of an unexpected situation, such as Kafka, Network failure, etc.
    FAILED
}