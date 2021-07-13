package com.aait.ezakbus.ui.adapters

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.ezakbus.R
import com.aait.ezakbus.models.city_liens_model.Line
import com.aait.ezakbus.ui.activities.bus.LinesActivity
import kotlinx.android.synthetic.main.line_city_item.view.*
import java.util.*

class LineAdapter : RecyclerView.Adapter<LineAdapter.LineVH>() {

    private var data: List<Line> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineVH {
        return LineVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.line_city_item, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LineVH, position: Int) = holder.bind(data[position])

    fun swapData(data: List<Line>) {
        this.data = data
        notifyDataSetChanged()
    }

    class LineVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item:Line) = with(itemView) {
            src_city.text=item.city1
            dist_city.text=item.city2
            details_to.text=item.name
            setOnClickListener {
                context.startActivity(Intent(context,LinesActivity::class.java).apply {
                    putExtra("line",item)
                }

                )
            }
        }
    }
}