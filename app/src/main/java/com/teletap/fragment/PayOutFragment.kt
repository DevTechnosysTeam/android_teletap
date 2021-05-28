package com.teletap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.teletap.R
import com.teletap.activity.DetailPayout
import com.teletap.adapter.AdapterEarnings
import com.teletap.adapter.AdapterPayOuts
import com.teletap.databinding.PayoutFragmentLayoutBinding

class PayOutFragment : Fragment(), AdapterPayOuts.OnItemClickListener {

    lateinit var binding : PayoutFragmentLayoutBinding
    lateinit var adapter : AdapterPayOuts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater, R.layout.payout_fragment_layout, container, false)

        binding.recyclerPayout.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { AdapterPayOuts(it, /*modelList,*/ this) }!!
        binding.recyclerPayout.adapter = adapter
        adapter.notifyDataSetChanged()


        return binding.root
    }

    override fun onAdapterItemClick(view: View?, index: Int) {
        val intent = Intent(activity, DetailPayout::class.java)
        startActivity(intent)
    }


}