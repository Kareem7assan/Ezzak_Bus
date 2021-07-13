package com.aait.ezakbus.ui.adapters

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.ezakbus.R
import com.aait.ezakbus.models.line_branch_model.Data
import com.aait.ezakbus.ui.activities.bus.ReserveBusActivity
import kotlinx.android.synthetic.main.item_adapter_details.view.*
import java.util.*

class LineAdapterDetails : RecyclerView.Adapter<LineAdapterDetails.LineVH>() {

    private var data: List<Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineVH {
        return LineVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_adapter_details, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LineVH, position: Int) = holder.bind(data[position],position,data[position]==data.last())

    fun swapData(data: List<Data>) {
        this.data = data
        notifyDataSetChanged()
    }

    class LineVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Data,
            position: Int,
            isLast: Boolean
        ) = with(itemView) {
            reach_tv.text=context.getString(R.string.reach)+" "+item.arrive_date_time
            src_city.text=item.start_address
            if (!isLast){
                view_up.visibility=View.VISIBLE
                view_down.visibility=View.GONE
            }
            else{
                view_up.visibility=View.VISIBLE
                view_down.visibility=View.VISIBLE
            }
            tv_duration.text=item.expected_time +" "+context.getString(R.string.mins)
            dist_city.text=item.end_address
            tv_price.text=item.traffic_line_price+" "+context.getString(R.string.rs)
            setOnClickListener {
                context.startActivity(Intent(context,ReserveBusActivity::class.java).apply {
                    putExtra("line_id",item.traffic_line_id.toString())
                    putExtra("arrival_time",item.arrive_date_time)
                    putExtra("order_id",item.id.toString())
                }
                )
            }
        }
    }
}