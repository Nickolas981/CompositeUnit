package com.example.compositeunit2.base

import com.example.compositeunit2.base.CompositeUnit

class SimpleCompositeUnit(override val clazz: Class<*>, override val layoutId: Int, override val spanSize: Int = 1) :
    CompositeUnit
