package com.example.compositeunit2.base

import android.view.View
import com.example.compositeunit2.adapter.MultiTypedDataBindingAdapter
import com.example.compositeunit2.adapter.MultiTypedPagedDataBindingAdapter

interface CompositeUnit {
    val clazz: Class<*>
    val layoutId: Int
    val compare: (Any, Any) -> Boolean
        get () = { _, _ -> false }
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

    fun toAdapter(): MultiTypedDataBindingAdapter<Any> =
        CompositeBuilder.build(this)
    fun toPagedAdapter(): MultiTypedPagedDataBindingAdapter<Any> =
        CompositeBuilder.buildPaged(this)
}
