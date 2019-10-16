package com.example.compositeunit2.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.compositeunit2.adapter.SimpleDataBindingViewHolder
import com.example.compositeunit2.adapter.SimpleViewHolder
import java.util.*

class AsyncLoadedCompositeUnit(
    private val compositeUnit: CompositeUnit,
    recyclerView: RecyclerView,
    size: Int
) : CompositeUnit by compositeUnit {

    private val asyncLayoutInflater: AsyncLayoutInflater = AsyncLayoutInflater(recyclerView.context)
    private val cachedViews: Stack<View> = Stack()

    init {
        for (i in 0..size) {
            asyncLayoutInflater.inflate(compositeUnit.layoutId, recyclerView) { view, _, _ ->
                cachedViews.push(view)
            }
        }
    }

    private fun getView(parent: ViewGroup): View {
        return if (cachedViews.isNullOrEmpty()) {
            LayoutInflater.from(parent.context).inflate(compositeUnit.layoutId, parent, false)
        } else {
            cachedViews.pop()
        }
    }

    override fun getViewHolder(parent: ViewGroup, recyclable: Boolean): SimpleViewHolder {
        val simpleViewHolder = if (binding) {
            SimpleDataBindingViewHolder(DataBindingUtil.bind(getView(parent))!!)
        } else {
            SimpleViewHolder(getView(parent))
        }
        createAction?.let { it(simpleViewHolder.itemView) }
        simpleViewHolder.setIsRecyclable(recyclable)
        return simpleViewHolder
    }

}
