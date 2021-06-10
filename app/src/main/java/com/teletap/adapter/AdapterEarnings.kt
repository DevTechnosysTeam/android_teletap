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
    //   private val onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.item_transition_history_layout, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding as ItemTransitionHistoryLayoutBinding
        binding.tvName.text = modelList[position].display_name
        binding.tvAmount.text = "Amount : "+modelList[position].currency_type + modelList[position].vendor_amount.toString()
        binding.transactionId.text = "#"+modelList[position].transaction_id

        binding.mainLayout.setOnClickListener { view ->
            onItemClickListener.onAdapterItemClick(view, holder.bindingAdapterPosition, modelList[holder.bindingAdapterPosition])
        }

    }

    interface OnItemClickListener {
        fun onAdapterItemClick(view: View?, index: Int, modelBean : ModelEarningsHome.DataBean.EarningDataBean)

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