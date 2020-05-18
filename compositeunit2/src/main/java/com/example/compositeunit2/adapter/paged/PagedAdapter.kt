package com.example.compositeunit2.adapter.paged

import com.example.compositeunit2.adapter.MultiViewTypeDataBindingAdapter
import com.example.compositeunit2.adapter.SimpleViewHolder
import com.example.compositeunit2.utils.autoNotify

abstract class PagedAdapter <T : Any>(
    private val types: Map<Class<*>, Int>, // Map for viewTypes
    private val handlers: Map<Int, Any> = mapOf(),
    private val compare: (T, T) -> Boolean,
    private val pagedConfig: PagedConfig
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

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == items.size - pagedConfig.offset) pagedConfig.onBottomReached(position)
    }
}
