package com.aait.ezakbus.ui.adapters

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aait.ezakbus.R
import com.aait.ezakbus.models.bus_menu.Order
import com.aait.ezakbus.ui.activities.bus.BookingDetailsActivity
import kotlinx.android.synthetic.main.item_adapter_details.view.*
import kotlinx.android.synthetic.main.line_city_item.view.dist_city
import kotlinx.android.synthetic.main.line_city_item.view.src_city
import java.util.*

class BusMenuAdapter : RecyclerView.Adapter<BusMenuAdapter.BusMenuVH>() {

    private var data: List<Order> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusMenuVH {
        return BusMenuVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_adapter_details, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: BusMenuVH, position: Int) = holder.bind(data[position],data[position]==data.last())

    fun swapData(data: List<Order>) {
        this.data = data
        notifyDataSetChanged()
    }

    class BusMenuVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Order, isLast: Boolean) = with(itemView) {
            reach_tv.text=context.getString(R.string.reach)+" "+item.arrive_time
            src_city.text=item.start_address
            if (!isLast){
                view_up.visibility=View.VISIBLE
                view_down.visibility=View.GONE
            }
            else{
                view_up.visibility=View.VISIBLE
                view_down.visibility=View.VISIBLE
            }
            tv_duration.text=item.expected_period +" "+context.getString(R.string.mins)
            dist_city.text=item.end_address
            tv_price.text=item.price.toString()+" "+context.getString(R.string.rs)
            setOnClickListener {
                context.startActivity(Intent(context,BookingDetailsActivity::class.java).apply {
                    putExtra("order_id",item.id.toString())
                })
            }
        }
    }
}