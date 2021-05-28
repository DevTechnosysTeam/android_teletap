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
import com.teletap.model.ModelContactList
import kotlin.collections.ArrayList

class AdapterContactList(private val context: Context, private val modelList: ArrayList<ModelContactList.DataBean>,
                         private val onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<AdapterContactList.MyViewHolder>() {
    //private var modelList = ArrayList<ModelChatThreads.ThreadBean>()
    //var context: Context
//    private val onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.item_contact_list_layout, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding as ItemContactListLayoutBinding

        binding.tvName.text = modelList[position].name
        binding.tvEmail.text = modelList[position].email

        binding.mainLayout.setOnClickListener { view ->
            onItemClickListener.onContactItemClick(view, holder.bindingAdapterPosition, modelList[holder.bindingAdapterPosition])
        }

    }

    interface OnItemClickListener {
        fun onContactItemClick(view: View?, index: Int, modelBean : ModelContactList.DataBean)
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