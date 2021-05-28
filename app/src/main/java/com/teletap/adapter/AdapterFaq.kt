package com.teletap.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.teletap.R
import com.teletap.databinding.ItemFaqLayoutBinding
import com.teletap.utilities.Animations

class AdapterFaq(
    private val context: Context /*private val modelList: ArrayList<ModelChatThreads.ThreadBean>,*/
    /*private val onItemClickListener: OnItemClickListener*/
)
    : RecyclerView.Adapter<AdapterFaq.MyViewHolder>() {
    //private var modelList = ArrayList<ModelChatThreads.ThreadBean>()
    //var context: Context
//    private val onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_faq_layout,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding as ItemFaqLayoutBinding

            binding.imgExpand.setOnClickListener {
                if (binding.ansRltv.visibility == View.VISIBLE){
                    binding.ansRltv.visibility = View.GONE
                    binding.imgExpand.rotation = 0F
                }else{
                    binding.ansRltv.visibility = View.VISIBLE
                    binding.imgExpand.rotation = 180F
                }
            }


        /*binding.layout.setOnClickListener { view ->
            onItemClickListener.onAdapterItemClick(view, holder.bindingAdapterPosition*//*, modelList[holder.adapterPosition]*//*)
        }*/

    }

    interface OnItemClickListener {
        fun onAdapterItemClick(view: View?, index: Int/*, modelBean : ModelChatThreads.ThreadBean*/)

    }

    override fun getItemCount(): Int {
        return /*modelList.size*/ 10
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

    private fun toggleLayout(isExpanded: Boolean, v: View, layoutExpand: RelativeLayout): Boolean {
        Animations.toggleArrow(v, isExpanded)
        if (isExpanded) {
            Animations.expand(layoutExpand)
        } else {
            Animations.collapse(layoutExpand)
        }
        return isExpanded
    }



}