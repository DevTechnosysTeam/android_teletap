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
import java.util.*

class AdapterCategory(private val context: Context, categoryListModels: ArrayList<ModelCategory.DataBean>,
                      onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<AdapterCategory.MyViewHolder>() {
    private var categoryListModels = ArrayList<ModelCategory.DataBean>()
    var onItemClick: ((ModelCategory.DataBean) -> Unit)? = null
    private val onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.layout_item_dialog, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding as LayoutItemDialogBinding
        binding.txtCountry.text = categoryListModels[position].name
        binding.llCountry.setOnClickListener { view -> onItemClickListener.onSelectedCategory(view, holder.adapterPosition, categoryListModels[holder.adapterPosition]) }
        val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int {
        return categoryListModels.size
    }

    inner class MyViewHolder internal constructor(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.executePendingBindings()
        }

    }

    init {
        this.categoryListModels = categoryListModels
        this.onItemClickListener = onItemClickListener

    }


    interface OnItemClickListener {
        fun onSelectedCategory(view: View?, index: Int, s: ModelCategory.DataBean)
    }

}