package com.lilcode.aop.p4c03.googlemap.activity

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lilcode.aop.p4c03.googlemap.R
import kotlinx.android.synthetic.main.viewpager_item.view.*

class ImgViewAdapter (imgList : ArrayList<Uri>) : RecyclerView.Adapter<ImgViewAdapter.PagerViewHolder>(){
    private var item = imgList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder = PagerViewHolder((parent))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.img.setImageURI(item[position])
//        holder.delete_button.setOnClickListener{
//            item.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, item.size)
//            notifyDataSetChanged()
//        }
    }

    override fun getItemCount(): Int = item.size


    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false)){
        val img = itemView.imageView_img!!
        //val delete_button = itemView.delete_img_button!!
    }

}