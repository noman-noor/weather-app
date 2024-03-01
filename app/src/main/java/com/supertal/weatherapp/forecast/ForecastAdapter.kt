package com.supertal.weatherapp.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.supertal.core.dataModels.AutoCompleteItem
import com.supertal.core.dataModels.ForecastUiData
import com.supertal.weatherapp.R
import com.supertal.weatherapp.databinding.ItemForcastDayBinding

class ForecastAdapter(
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private val forecastDataList: MutableList<ForecastUiData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding =
            ItemForcastDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    fun setList(data: List<ForecastUiData>) {
        forecastDataList.clear()
        forecastDataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return forecastDataList.size
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        forecastDataList.apply {
            val noteQuranItem = this[position]
            holder.bind(noteQuranItem, position)
        }
    }

    class ForecastViewHolder(
        val binding: ItemForcastDayBinding
    ) : ViewHolder(binding.root) {
        fun bind(
            dataItem: ForecastUiData, position: Int
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
            binding.executePendingBindings()
        }
    }
}

interface AutoCompleteClickListener {
    fun onClick(data: AutoCompleteItem)
}