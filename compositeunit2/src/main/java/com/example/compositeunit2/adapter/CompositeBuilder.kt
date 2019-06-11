package com.example.compositeunit2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
        val typesMap: MutableMap<Class<*>, Int> = mutableMapOf()
        val layoutMap: MutableMap<Int, Int> = mutableMapOf()
        val handlerMap: MutableMap<Int, Any> = mutableMapOf()
        val bindingMap: MutableMap<Int, Boolean> = mutableMapOf()
        val actionMap: MutableMap<Int, (View, Any) -> Unit> = mutableMapOf()
        val createActionMap: MutableMap<Int, (View) -> Unit> = mutableMapOf()
        val spanSizeMap: MutableMap<Int, Int> = mutableMapOf()

        units.forEachIndexed { index, unit ->
            typesMap[unit.clazz] = index
            layoutMap[index] = unit.layoutId
            unit.handler?.let { handlerMap[index] = it }
            bindingMap[index] = unit.binding
            unit.action?.let { actionMap[index] = it }
            unit.createAction?.let { createActionMap[index] = it }
            spanSizeMap[index] = unit.spanSize
            unit.createAction?.let { createActionMap[index] = it }
        }

        return object : MultiTypedDataBindingAdapter<Any>(
            typesMap, layoutMap, handlerMap, buildCompare()
        ) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                val simpleViewHolder = if (bindingMap[viewType] == true) {
                    super.onCreateViewHolder(parent, viewType)
                } else {
                    SimpleViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            layoutMap[viewType]!!,
                            parent,
                            false
                        )
                    )
                }
                createActionMap[viewType]?.let { it(simpleViewHolder.itemView) }
                return simpleViewHolder
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
        val layoutMap: MutableMap<Int, Int> = mutableMapOf()
        val handlerMap: MutableMap<Int, Any> = mutableMapOf()
        val bindingMap: MutableMap<Int, Boolean> = mutableMapOf()
        val actionMap: MutableMap<Int, (View, Any) -> Unit> = mutableMapOf()
        val createActionMap: MutableMap<Int, (View) -> Unit> = mutableMapOf()
        val spanSizeMap: MutableMap<Int, Int> = mutableMapOf()

        units.forEachIndexed { index, unit ->
            typesMap[unit.clazz] = index
            layoutMap[index] = unit.layoutId
            unit.handler?.let { handlerMap[index] = it }
            bindingMap[index] = unit.binding
            unit.action?.let { actionMap[index] = it }
            unit.createAction?.let { createActionMap[index] = it }
            spanSizeMap[index] = unit.spanSize
            unit.createAction?.let { createActionMap[index] = it }
        }

        return object : MultiTypedPagedDataBindingAdapter<Any>(
            typesMap, layoutMap, handlerMap, buildCompare()
        ) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
                val simpleViewHolder = if (bindingMap[viewType] == true) {
                    super.onCreateViewHolder(parent, viewType)
                } else {
                    SimpleViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            layoutMap[viewType]!!,
                            parent,
                            false
                        )
                    )
                }
                createActionMap[viewType]?.let { it(simpleViewHolder.itemView) }
                return simpleViewHolder
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
