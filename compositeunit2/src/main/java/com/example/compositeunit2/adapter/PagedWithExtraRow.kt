package com.example.compositeunit2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


const val EXTRA_ROW_TYPE = -13

open class PagedWithExtraRow<T : Any>(
    types: Map<Class<*>, Int>, // Map for viewTypes
    layoutIds: Map<Int, Int>, // Map for layoutIds
    handlers: Map<Int, Any> = mapOf(),
    compare: (T, T) -> Boolean,
    private val extraCU: CompositeUnit,
    private val extraItem: Any
) : MultiTypedPagedDataBindingAdapter<T>(types, layoutIds, handlers, compare) {


    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        if (position == itemCount - 1) {
            holder.bind(extraItem, extraCU.handler)
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return if (viewType == EXTRA_ROW_TYPE) {
            val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), extraCU.layoutId, parent, false
            )
            SimpleDataBindingViewHolder(binding)
        } else {
            super.onCreateViewHolder(parent, viewType)
        }
    }

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
