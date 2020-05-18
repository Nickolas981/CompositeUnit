package com.example.compositeunit2.base

import android.view.ViewGroup
import com.example.compositeunit2.adapter.*
import com.example.compositeunit2.adapter.paged.PagedAdapter
import com.example.compositeunit2.adapter.paged.PagedConfig
import com.example.compositeunit2.models.CompositeUnitData

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

            override fun onViewRecycled(holder: SimpleViewHolder) {
                super.onViewRecycled(holder)
                try {
                    val position = holder.adapterPosition
                    val viewType = getItemViewType(position)

                    cud.recyclingActionMap[viewType]?.invoke(position, items[position], holder.itemView)
                } catch (e: Exception) {
                }
            }

            override fun getSpanSize(viewType: Int): Int {
                return cud.spanSizeMap[viewType] ?: 1
            }
        }
    }

    fun buildPagedAdapter(pagedConfig: PagedConfig): PagedAdapter<Any> {
        val cud = CompositeUnitData.from(units)

        return object : PagedAdapter<Any>(
            cud.typesMap, cud.handlerMap, buildCompare(), pagedConfig
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

            override fun onViewRecycled(holder: SimpleViewHolder) {
                super.onViewRecycled(holder)
                try {
                    val position = holder.adapterPosition
                    val viewType = getItemViewType(position)

                    cud.recyclingActionMap[viewType]?.invoke(position, items[position], holder.itemView)
                } catch (e: Exception) {
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

            override fun onViewRecycled(holder: SimpleViewHolder) {
                super.onViewRecycled(holder)
                try {
                    val position = holder.adapterPosition
                    val viewType = getItemViewType(position)

                    cud.recyclingActionMap[viewType]?.invoke(position, currentList!![position]!!, holder.itemView)
                } catch (e: Exception) {
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

            override fun onViewRecycled(holder: SimpleViewHolder) {
                super.onViewRecycled(holder)
                try {
                    val position = holder.adapterPosition
                    val viewType = getItemViewType(position)

                    cud.recyclingActionMap[viewType]?.invoke(position, currentList!![position]!!, holder.itemView)
                } catch (e: Exception) {
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
