package com.teletap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.teletap.R
import com.teletap.databinding.LayoutItemDialogBinding
import com.teletap.model.ModelCategory
import com.teletap.model.ModelCity
import com.teletap.model.ModelCountryList
import com.teletap.model.ModelState
import java.util.*

class AdapterCity(private val context: Context, categoryListModels: ArrayList<ModelCity.DataBean>,
                  onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<AdapterCity.MyViewHolder>() {
    private var modelList = ArrayList<ModelCity.DataBean>()
    var onItemClick: ((ModelCity.DataBean) -> Unit)? = null
    private val onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.layout_item_dialog, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding as LayoutItemDialogBinding
        binding.txtCountry.text = modelList[position].name
        binding.llCountry.setOnClickListener { view -> onItemClickListener.onSelectedCity(view, holder.adapterPosition, modelList[holder.adapterPosition]) }
        val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    inner class MyViewHolder internal constructor(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.executePendingBindings()
        }

    }

    init {
        this.modelList = categoryListModels
        this.onItemClickListener = onItemClickListener

    }


    interface OnItemClickListener {
        fun onSelectedCity(view: View?, index: Int, s: ModelCity.DataBean)
    }

}