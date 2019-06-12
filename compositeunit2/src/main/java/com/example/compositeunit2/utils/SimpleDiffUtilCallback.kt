package com.example.compositeunit2.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class SimpleDiffUtilCallback<T : Any>(private val compare: (T, T) -> Boolean) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = compare(oldItem, newItem)

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}