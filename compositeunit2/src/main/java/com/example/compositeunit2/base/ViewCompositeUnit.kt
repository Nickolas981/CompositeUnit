package com.example.compositeunit2.base

import android.view.View
import android.view.ViewGroup
import com.example.compositeunit2.adapter.SimpleViewHolder

interface ViewCompositeUnit : CompositeUnit {
    override val layoutId: Int
        get() = 0

    fun getView(parent: ViewGroup): View

    override val binding: Boolean
        get() = false

    override fun getViewHolder(parent: ViewGroup, recyclable: Boolean): SimpleViewHolder {
        return SimpleViewHolder(getView(parent))
    }
}