package com.example.compositeunit2.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.compositeunit2.adapter.MultiTypedDataBindingAdapter
import com.example.compositeunit2.adapter.MultiTypedPagedDataBindingAdapter
import com.example.compositeunit2.adapter.SimpleDataBindingViewHolder
import com.example.compositeunit2.adapter.SimpleViewHolder

interface CompositeUnit {
    val clazz: Class<*>
    val layoutId: Int
    val compare: (Any, Any) -> Boolean
        get() = { _, _ -> false }
    val handler: Any?
        get() = null
    val action: ((View, Any) -> Unit)?
        get() = null
    val createAction: ((View) -> Unit)?
        get() = null
    val binding: Boolean
        get() = true
    val spanSize: Int
        get() = 1
    val preloadedViewHoldersSize: Int
        get() = 0

    fun toAdapter(): MultiTypedDataBindingAdapter<Any> =
        CompositeBuilder.build(this)

    fun toPagedAdapter(): MultiTypedPagedDataBindingAdapter<Any> =
        CompositeBuilder.buildPaged(this)

    open fun getViewHolder(parent: ViewGroup, recyclable: Boolean): SimpleViewHolder {
        val simpleViewHolder = if (binding) {
            val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false
            )
            SimpleDataBindingViewHolder(binding)
        } else {
            SimpleViewHolder(
                LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            )
        }
        createAction?.let { it(simpleViewHolder.itemView) }
        simpleViewHolder.setIsRecyclable(recyclable)
        return simpleViewHolder
    }
}
