package com.croman.kfood.outbox

interface OutboxScheduler {
    fun processMessage()
}