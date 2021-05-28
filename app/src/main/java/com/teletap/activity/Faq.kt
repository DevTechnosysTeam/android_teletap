package com.teletap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.teletap.R
import com.teletap.adapter.AdapterContactList
import com.teletap.adapter.AdapterFaq
import com.teletap.databinding.ActivityFaqBinding
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*

class Faq : AppCompatActivity() {
    lateinit var binding : ActivityFaqBinding
    lateinit var adapter : AdapterFaq

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq)

        binding.toolBar.imgBack.setOnClickListener { onBackPressed() }
        binding.toolBar.title.text = "Faq's"

        binding.recyclerView.layoutManager = LinearLayoutManager(this@Faq)
        adapter = AdapterFaq(this@Faq, /*modelList,*/);
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}