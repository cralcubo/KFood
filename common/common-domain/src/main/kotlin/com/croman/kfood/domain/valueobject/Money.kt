package com.croman.kfood.domain.valueobject

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money(val amount: BigDecimal) {

    companion object {
        val ZERO = Money(BigDecimal.ZERO)
    }

    fun isGreaterThanZero() =
        amount > BigDecimal.ZERO

    fun isGreaterThan(money: Money) =
        amount > money.amount

    fun add(money: Money) =
        Money((amount + money.amount).setScale())

    fun subtract(money: Money) =
        Money((amount - money.amount).setScale())

    fun multiply(multiplier: Int) =
        Money((amount * BigDecimal(multiplier)).setScale())

    private fun BigDecimal.setScale() =
        this.setScale(2, RoundingMode.HALF_EVEN)


}