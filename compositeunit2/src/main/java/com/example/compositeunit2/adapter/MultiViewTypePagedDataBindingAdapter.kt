package com.example.compositeunit2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import com.example.compositeunit2.BR
import com.example.compositeunit2.SimpleDiffUtilCallback


abstract class MultiViewTypePagedDataBindingAdapter<T : Any>(compare: (T, T) -> Boolean) :
    PagedListAdapter<T, MultiViewTypePagedDataBindingAdapter.SimpleViewHolder>(SimpleDiffUtilCallback<T>(compare)) {

    abstract fun getViewType(item: T): Int
    abstract fun getLayoutIdForViewType(viewType: Int): Int
    abstract fun getHandlerForViewType(viewType: Int): Any?
    abstract fun getSpanSize(viewType: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), getLayoutIdForViewType(viewType), parent, false
        )
        return SimpleDataBindingViewHolder(binding)
    }


    override fun getItemViewType(position: Int): Int {
        return getViewType(getItem(position)!!)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.bind(item, getHandlerForViewType(getViewType(item)))
    }

    open class SimpleViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        open fun bind(obj: Any = "", handler: Any? = "") {
        }
    }

    class SimpleDataBindingViewHolder(private val binding: ViewDataBinding) : SimpleViewHolder(binding.root) {
        override fun bind(obj: Any, handler: Any?) {
            binding.setVariable(BR.item, obj)
            handler?.let { binding.setVariable(BR.handler, it) }
            binding.executePendingBindings()
        }
    }
}

