package com.croman.kfood.saga


/**
 * Happy flow:
 * STARTED -> PROCESSING -> SUCCEEDED
 * Compensation flow:
 * STARTED -> PROCESSING -> COMPENSATING -> COMPENSATED
 * STARTED -> COMPENSATED
 */
enum class SagaStatus {
    STARTED,  // The name says everything
    PROCESSING, // Intermediate state, such as when the Payment is completed or the Order is sent to the Restaurant service for approval
    SUCCEEDED, // End of the saga flow
    COMPENSATING, // When an order is not approved by the restaurant service and a COMPENSATION flow needs to be started.
    COMPENSATED, // When a failure is registered and taken care of
    FAILED
}