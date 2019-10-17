package com.example.compositeunit2.models

import android.view.View
import android.view.ViewGroup
import com.example.compositeunit2.adapter.SimpleViewHolder
import com.example.compositeunit2.base.CompositeUnit

class CompositeUnitData(
    val typesMap: MutableMap<Class<*>, Int>,
    val handlerMap: MutableMap<Int, Any>,
    val bindingMap: MutableMap<Int, Boolean>,
    val actionMap: MutableMap<Int, (View, Any) -> Unit>,
    val spanSizeMap: MutableMap<Int, Int>,
    val getViewHolderMap: MutableMap<Int, (ViewGroup, Boolean) -> SimpleViewHolder>
) {
    companion object {
        fun from(units: List<CompositeUnit>): CompositeUnitData {
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

            return CompositeUnitData(
                typesMap,
                handlerMap,
                bindingMap,
                actionMap,
                spanSizeMap,
                getViewHolderMap
            )
        }
    }
}
