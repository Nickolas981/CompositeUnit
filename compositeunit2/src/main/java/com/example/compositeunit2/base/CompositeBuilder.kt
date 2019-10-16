package com.example.compositeunit2.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.compositeunit2.adapter.*
import com.example.compositeunit2.models.CompositeUnitData
import com.example.compositeunit2.utils.mapOf
import java.lang.ref.WeakReference
import java.util.*

class CompositeBuilder {
    private val units: MutableList<CompositeUnit> = mutableListOf()

    fun add(unit: CompositeUnit): CompositeBuilder {
        units.add(unit)
        return this
    }

    private fun buildCompare(): (Any, Any) -> Boolean {
        val map: MutableMap<Class<*>, (Any, Any) -> Boolean> = mutableMapOf()
        units.forEach {
            map[it.clazz] = it.compare
        }
        return { any, another ->
            val firstClass = any.javaClass
            if (firstClass == another.javaClass) {
                map[firstClass]!!(any, another)
            } else {
                false
            }
        }
    }

    fun build(): MultiTypedDataBindingAdapter<Any> {
        val cud = CompositeUnitData.from(units)

        return object : MultiTypedDataBindingAdapter<Any>(
            cud.typesMap, cud.handlerMap, buildCompare()
        ) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return cud.getViewHolderMap[viewType]!!(parent, true)
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                val viewType = getItemViewType(position)
                if (cud.bindingMap[viewType] == true) {
                    super.onBindViewHolder(holder, position)
                }
                cud.actionMap[viewType]?.let {
                    it(holder.itemView, items[position])
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return cud.spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPaged(): MultiTypedPagedDataBindingAdapter<Any> {
        val cud = CompositeUnitData.from(units)

        return object : MultiTypedPagedDataBindingAdapter<Any>(
            cud.typesMap, cud.handlerMap, buildCompare()
        ) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return cud.getViewHolderMap[viewType]!!(parent, true)
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                val viewType = getItemViewType(position)
                if (cud.bindingMap[viewType] == true) {
                    super.onBindViewHolder(holder, position)
                }
                cud.actionMap[viewType]?.let {
                    it(holder.itemView, getItem(position)!!)
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return cud.spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPaged(
        extraCU: CompositeUnit,
        extraItem: Any
    ): PagedWithExtraRow<Any> {
        val cud = CompositeUnitData.from(units)

        return object : PagedWithExtraRow<Any>(
            cud.typesMap, cud.handlerMap, buildCompare(), extraCU, extraItem
        ) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return if (viewType == EXTRA_ROW_TYPE) {
                    extraCU.getViewHolder(parent, true)
                } else {
                    cud.getViewHolderMap[viewType]!!(parent, true)
                }
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                if (position == itemCount - 1) {
                    holder.bind(extraItem, extraCU.handler)
                } else {
                    val viewType = getItemViewType(position)
                    if (cud.bindingMap[viewType] == true) {
                        super.onBindViewHolder(holder, position)
                    }
                    cud.actionMap[viewType]?.let {
                        it(holder.itemView, getItem(position)!!)
                    }
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return cud.spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPaged(
        extraCU: CompositeUnit,
        extraItem: Any,
        context: Context,
        recyclerView: WeakReference<RecyclerView>
    ): PagedWithExtraRow<Any> {
        val cud = CompositeUnitData.from(units)

        return object : PagedWithExtraRow<Any>(
            cud.typesMap, cud.handlerMap, buildCompare(), extraCU, extraItem
        ) {

            private val asyncLayoutInflater = AsyncLayoutInflater(context)
            private val cachedViews: Map<Int, Stack<View>> = mapOf(cud.layoutMap.keys) { Stack<View>() }

            init {
                cud.preloadedViewHoldersSizeMap.toList().filter { it.second != 0 }.forEach {
                    for (i in 0..it.second) {
                        asyncLayoutInflater.inflate(cud.layoutMap[it.first] ?: continue, recyclerView.get()) { view, _, _ ->
                            cachedViews[it.first]?.push(view)
                        }
                    }
                }
            }

            private fun getView(parent: ViewGroup, viewType: Int): View {
                val stack = cachedViews[viewType]
                return if (stack.isNullOrEmpty()) {
                    LayoutInflater.from(parent.context).inflate(cud.layoutMap[viewType]!!, parent, false)
                } else {
                    stack.pop()
                }
            }


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return if (viewType == EXTRA_ROW_TYPE) {
                    val binding: ViewDataBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), extraCU.layoutId, parent, false
                    )
                    SimpleDataBindingViewHolder(
                        binding
                    )
                } else {
                    val simpleViewHolder = if (cud.bindingMap[viewType] == true) {
                        SimpleDataBindingViewHolder(DataBindingUtil.bind(getView(parent, viewType))!!)
                    } else {
                        SimpleViewHolder(
                            getView(parent, viewType)
                        )
                    }
                    cud.createActionMap[viewType]?.let { it(simpleViewHolder.itemView) }
                    simpleViewHolder
                }
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                if (position == itemCount - 1) {
                    holder.bind(extraItem, extraCU.handler)
                } else {
                    val viewType = getItemViewType(position)
                    if (cud.bindingMap[viewType] == true) {
                        super.onBindViewHolder(holder, position)
                    }
                    cud.actionMap[viewType]?.let {
                        it(holder.itemView, getItem(position)!!)
                    }
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return cud.spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPaged(context: Context): MultiTypedPagedDataBindingAdapter<Any> {
        val cud = CompositeUnitData.from(units)

        return object : MultiTypedPagedDataBindingAdapter<Any>(
            cud.typesMap, cud.handlerMap, buildCompare()
        ) {

            private val asyncLayoutInflater = AsyncLayoutInflater(context)
            private val cachedViews: Map<Int, Stack<View>> = mapOf(cud.layoutMap.keys) { Stack<View>() }

            init {
                cud.preloadedViewHoldersSizeMap.toList().filter { it.second != 0 }.forEach {
                    for (i in 0..it.second) {
                        asyncLayoutInflater.inflate(cud.layoutMap[it.first] ?: continue, null) { view, _, _ ->
                            cachedViews[it.first]?.push(view)
                        }
                    }
                }
            }

            private fun getView(parent: ViewGroup, viewType: Int): View {
                val stack = cachedViews[viewType]
                return if (stack.isNullOrEmpty()) {
                    LayoutInflater.from(parent.context).inflate(cud.layoutMap[viewType]!!, parent, false)
                } else {
                    stack.pop()
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                val simpleViewHolder = if (cud.bindingMap[viewType] == true) {
                    SimpleDataBindingViewHolder(DataBindingUtil.bind(getView(parent, viewType))!!)
                } else {
                    SimpleViewHolder(
                        getView(parent, viewType)
                    )
                }
                cud.createActionMap[viewType]?.let { it(simpleViewHolder.itemView) }
                return simpleViewHolder
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                val viewType = getItemViewType(position)
                if (cud.bindingMap[viewType] == true) {
                    super.onBindViewHolder(holder, position)
                }
                cud.actionMap[viewType]?.let {
                    it(holder.itemView, getItem(position)!!)
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return cud.spanSizeMap[viewType] ?: 1
            }
        }
    }

    companion object {
        fun build(vararg units: CompositeUnit): MultiTypedDataBindingAdapter<Any> {
            return CompositeBuilder().apply {
                units.forEach { add(it) }
            }.build()
        }

        fun buildPaged(vararg units: CompositeUnit): MultiTypedPagedDataBindingAdapter<Any> {
            return CompositeBuilder().apply {
                units.forEach { add(it) }
            }.buildPaged()
        }
    }
}
