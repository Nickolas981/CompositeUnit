package com.example.compositeunit2.base

open class SimpleCompositeUnit(
    override val clazz: Class<*>,
    override val layoutId: Int,
    override val spanSize: Int = 1,
    override val binding: Boolean = true
) : CompositeUnit
