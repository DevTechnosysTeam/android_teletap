package com.teletap.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.teletap.R
import com.teletap.databinding.ItemContactListLayoutBinding
import com.teletap.databinding.ItemTransitionHistoryLayoutBinding
import com.teletap.model.ModelEarningsHome
import kotlin.collections.ArrayList

class AdapterEarnings(private val context: Context, private val modelList: ArrayList<ModelEarningsHome.DataBean.EarningDataBean>,
                      private val onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<AdapterEarnings.MyViewHolder>() {
    //private var modelList = ArrayList<ModelChatThreads.ThreadBean>()
    //var context: Context
//    private val onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.item_transition_history_layout, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding as ItemTransitionHistoryLayoutBinding
        binding.tvName.text = modelList[position].business_name
        binding.tvAmount.text = "Amount : AED "+modelList[position].total_amount.toString()
        binding.date.text = modelList[position].created

        /*binding.mainLayout.setOnClickListener { view ->
            onItemClickListener.onChatItemClick(view, holder.adapterPosition, modelList[holder.adapterPosition])
        }*/

    }

    interface OnItemClickListener {
        fun onAdapterItemClick(view: View?, index: Int/*, modelBean : ModelChatThreads.ThreadBean*/)

    }

    override fun getItemCount(): Int {
        return modelList.size

    }

    inner class MyViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.executePendingBindings()
        }
    }
    init {
//        image_list = list as ArrayList<ModelCategory>
        //this.context = context
//        this.onItemClickListener = onItemClickListener

    }



}