package com.example.compositeunit2.adapter

import com.example.compositeunit2.utils.autoNotify

abstract class MultiTypedDataBindingAdapter<T : Any>(
        private val types: Map<Class<*>, Int>, // Map for viewTypes
        private val handlers: Map<Int, Any> = mapOf(),
        private val compare: (T, T) -> Boolean
) : MultiViewTypeDataBindingAdapter<T>() {

    override var items: List<T> = emptyList()
        set(value) {
            autoNotify(items, value, compare) {
                field = value
            }
        }


    override fun getViewType(item: T): Int {
        return types[item::class.java]
                ?: throw  RuntimeException("There is no ViewType for ${item::class.java}")
    }

    override fun getHandlerForViewType(viewType: Int): Any? {
        return handlers[viewType]
    }

    override fun getSpanSize(viewType: Int): Int = 1
}
