package com.example.compositeunit2.adapter

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.compositeunit2.BR


abstract class MultiViewTypeDataBindingAdapter<T : Any>
    : androidx.recyclerview.widget.RecyclerView.Adapter<MultiViewTypeDataBindingAdapter.SimpleViewHolder>() {

    abstract var items: List<T>

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
        return getViewType(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = items[position]
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

