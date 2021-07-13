package com.aait.ezakbus.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.ezakbus.R
import com.aait.ezakbus.models.cities_model.CityModel
import kotlinx.android.synthetic.main.city_item.view.*
import java.util.*

class CityAdapter(val onAction:(item:CityModel)->Unit) : RecyclerView.Adapter<CityAdapter.CityVH>() {

    private var data: List<CityModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityVH {
        return CityVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.city_item, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CityVH, position: Int) = holder.bind(data[position],onAction)

    fun swapData(data: List<CityModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    class CityVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: CityModel,
            onAction: (item: CityModel) -> Unit
        ) = with(itemView) {
            tv_city.text=item.name
            setOnClickListener {
                onAction(item)
            }
        }
    }
}