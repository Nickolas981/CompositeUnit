package com.example.compositeunit2.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.compositeunit2.BR

open class SimpleViewHolder(view: View) : ViewHolder(view) {
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