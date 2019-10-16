package com.example.compositeunit2.adapter


abstract class MultiViewTypeDataBindingAdapter<T : Any>
    : androidx.recyclerview.widget.RecyclerView.Adapter<SimpleViewHolder>() {

    abstract var items: List<T>

    abstract fun getViewType(item: T): Int
    abstract fun getHandlerForViewType(viewType: Int): Any?
    abstract fun getSpanSize(viewType: Int): Int

    override fun getItemViewType(position: Int): Int {
        return getViewType(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, getHandlerForViewType(getViewType(item)))
    }
}

