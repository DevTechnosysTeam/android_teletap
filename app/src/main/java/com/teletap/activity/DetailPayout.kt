package com.teletap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.teletap.R
import com.teletap.databinding.DetailPayoutActivityBinding
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*

class DetailPayout : AppCompatActivity() {
    lateinit var binding : DetailPayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.detail_payout_activity)

        binding.toolBar.title.text = "Detail"
        binding.toolBar.imgBack.setOnClickListener { onBackPressed() }
    }
}