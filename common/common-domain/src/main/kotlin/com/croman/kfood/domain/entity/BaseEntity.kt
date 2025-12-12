package com.croman.kfood.domain.entity

abstract class BaseEntity<ID>(id: ID) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity<*>) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}