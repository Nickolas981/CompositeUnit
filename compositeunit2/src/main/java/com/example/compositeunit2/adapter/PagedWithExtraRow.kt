package com.example.compositeunit2.adapter

import com.example.compositeunit2.base.CompositeUnit


const val EXTRA_ROW_TYPE = -13

abstract class PagedWithExtraRow<T : Any>(
    types: Map<Class<*>, Int>, // Map for viewTypes
    handlers: Map<Int, Any> = mapOf(),
    compare: (T, T) -> Boolean,
    private val extraCU: CompositeUnit,
    private val extraItem: Any
) : MultiTypedPagedDataBindingAdapter<T>(types, handlers, compare) {

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            EXTRA_ROW_TYPE
        } else {
            super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

}
