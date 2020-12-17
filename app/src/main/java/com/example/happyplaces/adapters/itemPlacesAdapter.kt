package com.example.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.activities.AddHappyPlaces
import com.example.happyplaces.activities.MainActivity
import com.example.happyplaces.database.DataBaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_layout_row.view.*

class itemPlacesAdapter(val context: Context, val items: ArrayList<HappyPlaceModel>) :
    RecyclerView.Adapter<itemPlacesAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null


    fun setOnclickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to

        val image: CircleImageView = view.ivCircular
        val name: TextView = view.tvTitle
        val description = view.tvDescription

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_layout_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val uri = Uri.parse(item.image)

        holder.image.setImageURI(uri)
        holder.name.text = item.title
        holder.description.text = item.description

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(item , position )
            }
        }


    }

    fun notifyeditItem(activity:Activity , position:Int ,requestCode:Int){
        val intent = Intent( context ,AddHappyPlaces::class.java )
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS , items[position])
        activity.startActivityForResult(intent , requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(adapterPosition: Int) {
        val dbhandler = DataBaseHandler(context)
        val isDelete = dbhandler.deletehappyPlaceloyee(items[adapterPosition])
        if (isDelete>0){
            items.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
        }

    }

    interface  OnClickListener {
        fun onClick(model: HappyPlaceModel , position: Int)

    }


}