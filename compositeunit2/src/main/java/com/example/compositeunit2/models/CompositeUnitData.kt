package com.example.compositeunit2.models

import android.view.View
import com.example.compositeunit2.base.CompositeUnit

class CompositeUnitData(
    val typesMap: MutableMap<Class<*>, Int>,
    val layoutMap: MutableMap<Int, Int>,
    val handlerMap: MutableMap<Int, Any>,
    val bindingMap: MutableMap<Int, Boolean>,
    val actionMap: MutableMap<Int, (View, Any) -> Unit>,
    val createActionMap: MutableMap<Int, (View) -> Unit>,
    val spanSizeMap: MutableMap<Int, Int>,
    val preloadedViewHoldersSizeMap: MutableMap<Int, Int>
) {
    companion object {
        fun from(units: List<CompositeUnit>): CompositeUnitData {
            val typesMap: MutableMap<Class<*>, Int> = mutableMapOf()
            val layoutMap: MutableMap<Int, Int> = mutableMapOf()
            val handlerMap: MutableMap<Int, Any> = mutableMapOf()
            val bindingMap: MutableMap<Int, Boolean> = mutableMapOf()
            val actionMap: MutableMap<Int, (View, Any) -> Unit> = mutableMapOf()
            val createActionMap: MutableMap<Int, (View) -> Unit> = mutableMapOf()
            val spanSizeMap: MutableMap<Int, Int> = mutableMapOf()
            val preloadedViewHoldersSizeMap: MutableMap<Int, Int> = mutableMapOf()

            units.forEachIndexed { index, unit ->
                typesMap[unit.clazz] = index
                layoutMap[index] = unit.layoutId
                unit.handler?.let { handlerMap[index] = it }
                bindingMap[index] = unit.binding
                unit.action?.let { actionMap[index] = it }
                unit.createAction?.let { createActionMap[index] = it }
                spanSizeMap[index] = unit.spanSize
                unit.createAction?.let { createActionMap[index] = it }
                preloadedViewHoldersSizeMap[index] = unit.preloadedViewHoldersSize
            }

            return CompositeUnitData(
                typesMap,
                layoutMap,
                handlerMap,
                bindingMap,
                actionMap,
                createActionMap,
                spanSizeMap,
                preloadedViewHoldersSizeMap
            )
        }
    }
}
