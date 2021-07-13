package com.aait.ezakbus.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aait.ezakbus.R
import com.aait.ezakbus.models.city_liens_model.Days
import kotlinx.android.synthetic.main.item_day.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class DayAdapter(val onDayClick:(item:Days)->Unit) : RecyclerView.Adapter<DayAdapter.DayVH>() {

    private var data: List<Days> = ArrayList()

    private var selected_positon=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayVH {
        return DayVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_day, parent, false)
        )
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: DayVH, position: Int) {
        holder.bind(data[position])
        with(holder.itemView) {
            if (selected_positon == position) {
                details_lay_start.background=
                    ContextCompat.getDrawable(context,R.drawable.between_cities_dark)
                tv_day.setTextColor(ContextCompat.getColor(context,android.R.color.white))
            }
            else {
                details_lay_start.background=
                    ContextCompat.getDrawable(context,R.drawable.between_cities_bh)
                tv_day.setTextColor(ContextCompat.getColor(context,android.R.color.black))
            }
            holder.itemView.onClick {
                onDayClick(data[position])
                selected_positon=position
                notifyDataSetChanged()
            }
        }
    }

    fun swapData(data: List<Days>) {
        this.data = data
        notifyDataSetChanged()
    }

    class DayVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Days) = with(itemView) {
            tv_day.text=item.name
            setOnClickListener {
                // TODO: Handle on click
            }
        }
    }
}