package com.isanechek.extractx.core.common

interface Mapper<T, F> {
    fun map(from: F): T
    fun map(from: List<F>): List<T>
}