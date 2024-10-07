package com.example.vendingmachineinventorymanagement.utils.baseclasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<B : ViewDataBinding, T>(
    private val itemLayout: Int,
    val items: List<T>
) : RecyclerView.Adapter<BaseAdapter.StringHolder<B>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringHolder<B> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<B>(inflater, itemLayout, parent, false)
        return StringHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StringHolder<B>, position: Int) {
        bindView(holder.viewDataBinding, items[position], position)
    }

    abstract fun bindView(binding: B, item: T, position: Int)

    class StringHolder<B : ViewDataBinding>(val viewDataBinding: B) :
        RecyclerView.ViewHolder(viewDataBinding.root)
}