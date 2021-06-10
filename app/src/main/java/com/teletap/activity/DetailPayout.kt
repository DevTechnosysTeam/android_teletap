package com.teletap.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.DetailPayoutActivityBinding
import com.teletap.model.DetailsPayoutModel
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.PayoutDetailsPresenter
import com.teletap.utilities.Utility
import com.teletap.view.IDetailsPayoutView
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class DetailPayout : BaseActivity(), IDetailsPayoutView {
    lateinit var binding : DetailPayoutActivityBinding
    lateinit var presenterDetails : PayoutDetailsPresenter

    lateinit var userInfo: SignupModel.DataBean
    private var token : String = ""

    private var transaction_id:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.detail_payout_activity)
        presenterDetails = PayoutDetailsPresenter()
        presenterDetails.view = this

        if (intent!=null){
            transaction_id = intent.getStringExtra("transaction_id").toString()
        }

        try {
            userInfo = AppPreference.getUserInfo(context)
            Log.e("userInfo", userInfo.id.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }
        token = userInfo.token.toString()

        binding.toolBar.title.text = getString(R.string.detail)
        binding.toolBar.imgBack.setOnClickListener { onBackPressed() }

        payoutDetailsApi()
    }

    private fun payoutDetailsApi(){
        if (Utility.hasConnection(this@DetailPayout)) {
            val map: MutableMap<String, Any> = HashMap()
            map["transaction_id"] = ""+transaction_id
            map["token"] = ""+token
            presenterDetails.payoutDetailsApi(this@DetailPayout, map)
        } else {
            Utility.showToast(this@DetailPayout, getString(R.string.no_network_message))
        }
    }

    override fun onSuccess(body: DetailsPayoutModel?) {
        val status = body!!.status
        if (status == 1) {
            binding.tvTransactionId.text = "#"+body.data?.transaction_id
            binding.tvTax.text = body.data?.tax.toString()+"%"
            binding.name.text = body.data?.display_name.toString()
            binding.tvExchangeCharge.text = body.data?.currency_exchange_percent.toString()+"%"

            binding.tvTotalAmount.text = body.data?.currency_type+body.data?.vendor_amount
            binding.tvDate.text = body.data?.created_date

        }else{
            Utility.shortToast(this@DetailPayout, body.message)
        }
    }

    override fun getContext(): Context {
        return this
    }
}