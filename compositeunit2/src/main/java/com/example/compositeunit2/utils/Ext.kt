package com.example.compositeunit2.utils

fun <T, R> mapOf(list: Iterable<T>, block: (T) -> R): Map<T, R> {
    val result = mutableMapOf<T, R>()
    list.forEach {
        result[it] = block(it)
    }
    return result
}
