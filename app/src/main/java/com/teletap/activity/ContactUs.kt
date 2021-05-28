package com.teletap.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.ContactUsActivityBinding
import com.teletap.model.SignupModel
import com.teletap.model.SimpleModel
import com.teletap.presenterClasses.ContactUsPresenter
import com.teletap.utilities.Utility
import com.teletap.view.IContactUsView
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*
import java.util.HashMap

class ContactUs : BaseActivity(), IContactUsView {

    lateinit var binding: ContactUsActivityBinding
    lateinit var presenterContact: ContactUsPresenter
    lateinit var userInfo: SignupModel.DataBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.contact_us_activity)
        presenterContact = ContactUsPresenter()
        presenterContact.view = this

        binding.toolBar.title.text = getString(R.string.contact_us)
        binding.toolBar.imgBack.setOnClickListener { onBackPressed() }

        try {
            userInfo = AppPreference.getUserInfo(this@ContactUs)
            Log.e("userInfo", userInfo.token.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        binding.btnSubmitC.setOnClickListener {
            Utility.hideKeyboard(this@ContactUs)
            checkValidation()
        }
    }

    private fun checkValidation() {
        if (TextUtils.isEmpty(binding.edNameC.text.toString().trim())) {
            Toast.makeText(this@ContactUs, "Please enter your name", Toast.LENGTH_SHORT).show()
            binding.edNameC.requestFocus()
        }else if (TextUtils.isEmpty(binding.edEmailC.text.toString().trim())) {
            Toast.makeText(this@ContactUs, "Please enter email", Toast.LENGTH_SHORT).show()
            binding.edEmailC.requestFocus()
        }else if (!Utility.isValidEmail(binding.edEmailC.text.toString().trim())) {
            Toast.makeText(this@ContactUs, "Please enter valid email", Toast.LENGTH_SHORT).show()
            binding.edEmailC.requestFocus()
        }else if (TextUtils.isEmpty(binding.edSubjectC.text.toString().trim())) {
            Toast.makeText(this@ContactUs, "Please enter subject", Toast.LENGTH_SHORT).show()
            binding.edSubjectC.requestFocus()
        }else if (TextUtils.isEmpty(binding.edDescriptionC.text.toString().trim())) {
            Toast.makeText(this@ContactUs, "Please enter description", Toast.LENGTH_SHORT).show()
            binding.edDescriptionC.requestFocus()
        }else{
            if (Utility.hasConnection(this@ContactUs)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["email"]= ""+binding.edEmailC.text.toString().trim()
                map["name"]= ""+binding.edNameC.text.toString().trim()
                map["subject"]= ""+binding.edSubjectC.text.toString().trim()
                map["description"]= ""+binding.edDescriptionC.text.toString().trim()
                presenterContact.callContactUsApi(this@ContactUs, map)
            }else{
                Utility.showToast(this@ContactUs, getString(R.string.no_network_message))
            }
        }
    }


    override fun onSuccess(body: SimpleModel?) {
        val status = body!!.status
        if (status == 1) {
            Utility.shortToast(this@ContactUs, body.message)
            finish()
        }else{
            Utility.shortToast(this@ContactUs, body.message)
        }
    }

    override fun getContext(): Context {
        return this
    }
}