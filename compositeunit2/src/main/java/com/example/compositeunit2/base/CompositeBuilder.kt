package com.example.compositeunit2.base

import android.view.View
import android.view.ViewGroup
import com.example.compositeunit2.adapter.*

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


    fun build(recyclable: Boolean = true): MultiTypedDataBindingAdapter<Any> {
        val typesMap: MutableMap<Class<*>, Int> = mutableMapOf()
        val handlerMap: MutableMap<Int, Any> = mutableMapOf()
        val bindingMap: MutableMap<Int, Boolean> = mutableMapOf()
        val actionMap: MutableMap<Int, (View, Any) -> Unit> = mutableMapOf()
        val spanSizeMap: MutableMap<Int, Int> = mutableMapOf()
        val getViewHolderMap: MutableMap<Int, (ViewGroup, Boolean) -> SimpleViewHolder> =
            mutableMapOf()

        units.forEachIndexed { index, unit ->
            typesMap[unit.clazz] = index
            unit.handler?.let { handlerMap[index] = it }
            bindingMap[index] = unit.binding
            unit.action?.let { actionMap[index] = it }
            spanSizeMap[index] = unit.spanSize
            getViewHolderMap[index] = unit::getViewHolder
        }

        return object : MultiTypedDataBindingAdapter<Any>(
            typesMap, handlerMap, buildCompare()
        ) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return getViewHolderMap[viewType]!!(parent, recyclable)
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                val viewType = getItemViewType(position)
                if (bindingMap[viewType] == true) {
                    super.onBindViewHolder(holder, position)
                }
                actionMap[viewType]?.let {
                    it(holder.itemView, items[position])
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPaged(): MultiTypedPagedDataBindingAdapter<Any> {
        val typesMap: MutableMap<Class<*>, Int> = mutableMapOf()
        val handlerMap: MutableMap<Int, Any> = mutableMapOf()
        val bindingMap: MutableMap<Int, Boolean> = mutableMapOf()
        val actionMap: MutableMap<Int, (View, Any) -> Unit> = mutableMapOf()
        val spanSizeMap: MutableMap<Int, Int> = mutableMapOf()
        val getViewHolderMap: MutableMap<Int, (ViewGroup, Boolean) -> SimpleViewHolder> =
            mutableMapOf()

        units.forEachIndexed { index, unit ->
            typesMap[unit.clazz] = index
            unit.handler?.let { handlerMap[index] = it }
            bindingMap[index] = unit.binding
            unit.action?.let { actionMap[index] = it }
            spanSizeMap[index] = unit.spanSize
            getViewHolderMap[index] = unit::getViewHolder
        }

        return object :
            MultiTypedPagedDataBindingAdapter<Any>(typesMap, handlerMap, buildCompare()) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return getViewHolderMap[viewType]!!(parent, true)
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                val viewType = getItemViewType(position)
                if (bindingMap[viewType] == true) {
                    super.onBindViewHolder(holder, position)
                }
                actionMap[viewType]?.let {
                    it(holder.itemView, getItem(position)!!)
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPaged(
        extraCU: CompositeUnit,
        extraItem: Any
    ): PagedWithExtraRow<Any> {
        val typesMap: MutableMap<Class<*>, Int> = mutableMapOf()
        val handlerMap: MutableMap<Int, Any> = mutableMapOf()
        val bindingMap: MutableMap<Int, Boolean> = mutableMapOf()
        val actionMap: MutableMap<Int, (View, Any) -> Unit> = mutableMapOf()
        val spanSizeMap: MutableMap<Int, Int> = mutableMapOf()
        val getViewHolderMap: MutableMap<Int, (ViewGroup, Boolean) -> SimpleViewHolder> =
            mutableMapOf()

        units.forEachIndexed { index, unit ->
            typesMap[unit.clazz] = index
            unit.handler?.let { handlerMap[index] = it }
            bindingMap[index] = unit.binding
            unit.action?.let { actionMap[index] = it }
            spanSizeMap[index] = unit.spanSize
            getViewHolderMap[index] = unit::getViewHolder
        }

        return object :
            PagedWithExtraRow<Any>(typesMap, handlerMap, buildCompare(), extraCU, extraItem) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                return if (viewType == EXTRA_ROW_TYPE) {
                    extraCU.getViewHolder(parent, true)
                } else {
                    getViewHolderMap[viewType]!!(parent, true)
                }
            }

            override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
                if (position == itemCount - 1) {
                    holder.bind(extraItem, extraCU.handler)
                } else {
                    val viewType = getItemViewType(position)
                    if (bindingMap[viewType] == true) {
                        super.onBindViewHolder(holder, position)
                    }
                    actionMap[viewType]?.let {
                        it(holder.itemView, getItem(position)!!)
                    }
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return spanSizeMap[viewType] ?: 1
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
