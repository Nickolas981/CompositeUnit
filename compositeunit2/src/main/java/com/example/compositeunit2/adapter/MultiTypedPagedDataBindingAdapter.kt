package com.example.compositeunit2.adapter

open class MultiTypedPagedDataBindingAdapter<T : Any>(
    private val types: Map<Class<*>, Int>, // Map for viewTypes
    private val layoutIds: Map<Int, Int>, // Map for layoutIds
    private val handlers: Map<Int, Any> = mapOf(),
    compare: (T, T) -> Boolean
) : MultiViewTypePagedDataBindingAdapter<T>(compare) {

    override fun getViewType(item: T): Int {
        return types[item::class.java]
            ?: throw  RuntimeException("There is no ViewType for ${item::class.java}")
    }

    override fun getHandlerForViewType(viewType: Int): Any? {
        return handlers[viewType]
    }

    override fun getLayoutIdForViewType(viewType: Int): Int {
        return layoutIds[viewType]
            ?: throw RuntimeException("There is no layoutId for viewType:$viewType")
    }

    override fun getSpanSize(viewType: Int): Int = 1

}
