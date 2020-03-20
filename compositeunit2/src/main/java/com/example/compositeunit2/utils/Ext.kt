package com.example.compositeunit2.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.compositeunit2.adapter.MultiTypedDataBindingAdapter

fun <T, R> mapOf(list: Iterable<T>, block: (T) -> R): Map<T, R> {
    val result = mutableMapOf<T, R>()
    list.forEach {
        result[it] = block(it)
    }
    return result
}

@BindingAdapter("items")
fun RecyclerView.setItems(items: List<Any>) {
    (adapter as? MultiTypedDataBindingAdapter<Any>)?.items = items
}