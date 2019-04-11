package com.example.compositeunit2.adapter

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


fun <T> androidx.recyclerview.widget.RecyclerView.Adapter<*>.autoNotify(
    oldList: List<T>,
    newList: List<T>,
    compare: (T, T) -> Boolean,
    callback: () -> Unit = {}
) {
    if (oldList.isEmpty() && newList.isNotEmpty()) {
        callback()
        notifyItemRangeInserted(0, newList.size)
    } else {
        Thread {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return compare(oldList[oldItemPosition], newList[newItemPosition])
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldList[oldItemPosition] == newList[newItemPosition]
                }

                override fun getOldListSize() = oldList.size

                override fun getNewListSize() = newList.size
            })
            Handler(Looper.getMainLooper()).post {
                callback()
                diff.dispatchUpdatesTo(this@autoNotify)
            }
        }.start()
    }
}