package com.lilcode.aop.p4c03.googlemap.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lilcode.aop.p4c03.googlemap.R
import kotlinx.android.synthetic.main.viewpager_item.view.*

class PostViewAdapter (imgList : ArrayList<String>) : RecyclerView.Adapter<PostViewAdapter.PagerViewHolder>(){
    private var item = imgList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder = PagerViewHolder((parent))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.setImageView(item.get(position))
    }

    override fun getItemCount(): Int = item.size


    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.postpager_item, parent, false)){
        val img = itemView.imageView_img!!
        val pc = parent.context

        fun setImageView(imgURL: String){
            Glide.with(pc).load(imgURL).into(img)
        }
    }

}