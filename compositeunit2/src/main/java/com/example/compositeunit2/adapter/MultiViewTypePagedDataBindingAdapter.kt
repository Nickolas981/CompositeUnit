package com.example.compositeunit2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import com.example.compositeunit2.BR
import com.example.compositeunit2.utils.SimpleDiffUtilCallback


abstract class MultiViewTypePagedDataBindingAdapter<T : Any>(compare: (T, T) -> Boolean) :
    PagedListAdapter<T, SimpleViewHolder>(
        SimpleDiffUtilCallback<T>(
            compare
        )
    ) {

    abstract fun getViewType(item: T): Int
    abstract fun getHandlerForViewType(viewType: Int): Any?
    abstract fun getSpanSize(viewType: Int): Int

    override fun getItemViewType(position: Int): Int {
        return getViewType(getItem(position)!!)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.bind(item, getHandlerForViewType(getViewType(item)))
    }
}

