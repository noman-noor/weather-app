package com.supertal.weatherapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.supertal.weatherapp.core.dataModels.AutoCompleteItem
import com.supertal.weatherapp.R
import com.supertal.weatherapp.databinding.ItemAutoCompleteBinding

class AutCompleteAdapter(
    private val clickListener: AutoCompleteClickListener
) : RecyclerView.Adapter<AutCompleteAdapter.MyViewHolder>() {
    private val autoCompleteDataList: MutableList<AutoCompleteItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemAutoCompleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, clickListener)
    }

    fun setList(data: List<AutoCompleteItem>) {
        autoCompleteDataList.clear()
        autoCompleteDataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return autoCompleteDataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        autoCompleteDataList.apply {
            val noteQuranItem = this[position]
            holder.bind(noteQuranItem, position)
        }
    }

    class MyViewHolder(
        val binding: ItemAutoCompleteBinding, private val clickListener: AutoCompleteClickListener
    ) : ViewHolder(binding.root) {
        fun bind(
            dataItem: AutoCompleteItem, position: Int
        ) {

            if (position % 2 == 0) binding.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    binding.itemView.context, R.color.white
                )
            )
            else binding.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    binding.itemView.context, R.color.white_dim
                )
            )
            binding.data = dataItem
            binding.itemView.setOnClickListener {
                clickListener.onClick(dataItem)
            }
            binding.executePendingBindings()
        }
    }
}

interface AutoCompleteClickListener {
    fun onClick(data: AutoCompleteItem)
}