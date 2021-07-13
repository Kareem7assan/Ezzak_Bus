package com.aait.ezakbus.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aait.ezakbus.R
import com.aait.ezakbus.models.line_points.Point
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class Horizental_Step_View(val onSelected:(start_selected_pos:Int,end_selected_pos:Int,action:Action)->Unit): RecyclerView.Adapter<Horizental_Step_View.HorizentalVH>() {
    var ITEM_TYPE=1
    var action=Action.NOT_SELECTED
    private var data: List<Point> = ArrayList()
    private var start_selected_pos=-1
    private var end_selected_pos=-1

    enum class Action{
         NOT_SELECTED,
         START_SELECTED,
         END_SELECTED
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizentalVH {
        if (ITEM_TYPE==1){
            return HorizentalVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.horizental_item, parent, false)
            )
        }
        else if (ITEM_TYPE==2){
            return HorizentalVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.horizental_bottom_item, parent, false)
            )
        }
        else{
            return HorizentalVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.horizental_normal_item, parent, false)
            )
        }

    }

    override fun getItemCount() = data.size

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: HorizentalVH, position: Int) {
        holder.bind(data[position])
        with(holder.itemView){
            if (start_selected_pos>-1||end_selected_pos>-1){
                //normal
                holder.tv_point?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                holder.tv_point?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                //top
                holder.tv_point_top?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                //bottom
                holder.tv_point_bottom?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
            }
            else{
                //normal
                holder.tv_point?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                holder.tv_point?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                //top
                holder.tv_point_top?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                //bottom
                holder.tv_point_bottom?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,R.color.dark))
            }
            holder.tv_point?.text=data[position].name+","+data[position].address
            holder.tv_point_top?.text=data[position].name+","+data[position].address
            holder.tv_point_bottom?.text=data[position].name+","+data[position].address

            onClick {
                if (action==Action.NOT_SELECTED){
                    start_selected_pos=position
                    action=Action.START_SELECTED
                    //normal
                    holder.tv_point?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                    holder.tv_point?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                    //top
                    holder.tv_point_top?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                    holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                    //bottom
                    holder.tv_point_bottom?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                    holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,android.R.color.white))

                }
                else if (action==Action.START_SELECTED){
                    if (position!=start_selected_pos) {
                        end_selected_pos = position
                        action = Action.END_SELECTED
                        //normal
                        holder.tv_point?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                        holder.tv_point?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                        //top
                        holder.tv_point_top?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                        holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                        //bottom
                        holder.tv_point_bottom?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                        holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                    }
                    else{
                        start_selected_pos=-1
                        action = Action.NOT_SELECTED
                        //normal
                        holder.tv_point?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                        holder.tv_point?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                        //top
                        holder.tv_point_top?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                        holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                        //bottom
                        holder.tv_point_bottom?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                        holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                    }
                }
                else if (action==Action.END_SELECTED){
                    if (position!=end_selected_pos && end_selected_pos==-1) {
                        end_selected_pos = position
                        action = Action.END_SELECTED

                        //normal
                        holder.tv_point?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                        holder.tv_point?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                        //top
                        holder.tv_point_top?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                        holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                        //bottom
                        holder.tv_point_bottom?.backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.bg_bus)
                        holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,android.R.color.white))
                    }
                    else if (position==end_selected_pos){

                        end_selected_pos = -1
                        action = Action.START_SELECTED

                        //normal
                        holder.tv_point?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                        holder.tv_point?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                        //top
                        holder.tv_point_top?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                        holder.tv_point_top?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                        //bottom
                        holder.tv_point_bottom?.backgroundDrawable=context.getDrawable(R.drawable.between_cities_bh)
                        holder.tv_point_bottom?.setTextColor(ContextCompat.getColor(context,R.color.dark))
                    }

                }
                onSelected(start_selected_pos, end_selected_pos, action)
            }
        }
    }

    fun swapData(data: List<Point>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (data.isNotEmpty()) {
            if (position == 0) {
                ITEM_TYPE = 1
            }
            else if (data[position] == data.last()) {
                ITEM_TYPE = 2
            }
            else {
                ITEM_TYPE = 3
            }
        }
        return ITEM_TYPE
    }

    class HorizentalVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_point_top:TextView? = null
        var tv_point_bottom:TextView? = null
        var tv_point:TextView? = null
        fun bind(item:Point) = with(itemView) {
            tv_point_top=findOptional(R.id.tv_top)
            tv_point=findOptional(R.id.tv_point_normal)
            tv_point_bottom=findOptional(R.id.tv_point_bottom)
            setOnClickListener {

            }
        }
    }
}