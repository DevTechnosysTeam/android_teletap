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
import com.teletap.model.ModelDetailsContact
import kotlin.collections.ArrayList

class AdapterTransitionHistoryCD(private val context: Context, private val modelList: ArrayList<ModelDetailsContact.DataBean.TransactionHistoryBean>,
                                 private val onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<AdapterTransitionHistoryCD.MyViewHolder>() {
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
        binding.tvName.text = modelList[position].transaction_details?.business_name
        binding.transactionId.text = modelList[position].transaction_details?.transaction_id

        binding.tvAmount.text = "Amount :"+modelList[position].transaction_details?.currency_type+modelList[position].transaction_details?.vendor_amount

        binding.mainLayout.setOnClickListener { view ->
            onItemClickListener.onAdapterItemClick(view, holder.bindingAdapterPosition, modelList[holder.bindingAdapterPosition])
        }

    }

    interface OnItemClickListener {
        fun onAdapterItemClick(view: View?, index: Int, modelBean : ModelDetailsContact.DataBean.TransactionHistoryBean)

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