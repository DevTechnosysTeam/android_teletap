package com.teletap.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.Session
import com.teletap.databinding.OtpVerificationActivityBinding
import com.teletap.model.SignupModel
import com.teletap.model.VerifyOtpModel
import com.teletap.presenterClasses.VerifyOtpPresenter
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.IVerifyView
import java.util.*

class OtpVerification : BaseActivity(), IVerifyView {
    lateinit var binding : OtpVerificationActivityBinding
    lateinit var  presnter: VerifyOtpPresenter

    private var user_id :Int = 0
    private var isNumberVerified : Boolean = false
    private var cameFrom :String =""
    private var email :String =""
    private var deviceToken : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.otp_verification_activity)
        presnter = VerifyOtpPresenter()
        getFcmToken()
        presnter.view = this
        if (intent!=null){
            user_id = intent.getIntExtra("user_id", 0)
            cameFrom = intent.getStringExtra("cameFrom").toString()
            isNumberVerified = intent.getBooleanExtra("isNumberVerified", false)
            if (cameFrom == "forgetPassword"){
                email = intent.getStringExtra("email").toString()
            }
        }

        binding.back.setOnClickListener { onBackPressed() }
    }

    fun onclick(view: View) {
        when(view.id){
            R.id.btnSubmit -> {
                Utility.hideKeyboard(this@OtpVerification)
                /*val intent = Intent(this@OtpVerification, HomeActivity::class.java)
                startActivity(intent)*/

                if (TextUtils.isEmpty(binding.pinView.text.toString())) {
                    Toast.makeText(this@OtpVerification, "Please enter otp", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (cameFrom == "signUp" && !isNumberVerified) {
                        if (Utility.hasConnection(this)) {
                            val map: MutableMap<String, Any> = HashMap()
                            map["sms_otp"] = binding.pinView.text.toString()
                            map["user_id"] = "" + user_id
                            map["language"] =
                                "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                            presnter.verifyAccount(this@OtpVerification, map)
                        } else {
                            Utility.showToast(this, getString(R.string.no_network_message))
                        }
                    } else if (cameFrom == "login" && isNumberVerified) {
                        if (Utility.hasConnection(this)) {
                            val map: MutableMap<String, Any> = HashMap()
                            map["sms_otp"] = binding.pinView.text.toString()
                            map["user_id"] = "" + user_id
                            map["device_type"] = "" + "1"
                            map["device_token"] = "" + deviceToken
                            map["language"] =
                                "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                            presnter.verifyLoginOtp(this@OtpVerification, map)
                        } else {
                            Utility.showToast(this, getString(R.string.no_network_message))
                        }
                    }else if (cameFrom == "forgetPassword" && isNumberVerified) {
                        if (Utility.hasConnection(this)) {
                            val map: MutableMap<String, Any> = HashMap()
                            map["otp"] = binding.pinView.text.toString()
                            map["user_id"] = "" + user_id
                            //map["email"] = "" + email
                            map["language"] =
                                "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                            presnter.verifyForgetOtp(this@OtpVerification, map)
                        } else {
                            Utility.showToast(this, getString(R.string.no_network_message))
                        }
                    }else if (cameFrom == "googleLogin" && !isNumberVerified){
                        if (Utility.hasConnection(this)) {
                            val map: MutableMap<String, Any> = HashMap()
                            map["sms_otp"] = binding.pinView.text.toString()
                            map["user_id"] = "" + user_id
                            map["language"] =
                                "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                            presnter.verifyAccount(this@OtpVerification, map)
                        } else {
                            Utility.showToast(this, getString(R.string.no_network_message))
                        }
                    }
                }

            }
            R.id.resend_otp -> {
                if (/*cameFrom == "signUp"*/ !isNumberVerified) {
                    if (Utility.hasConnection(this)) {
                        val map: MutableMap<String, Any> = HashMap()
                        map["user_id"] = "" + user_id
                        map["language"] =
                            "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                        presnter.resendOtp(this@OtpVerification, map)
                    } else {
                        Utility.showToast(this, getString(R.string.no_network_message))
                    }
                } else if (cameFrom == "login" && isNumberVerified) {
                    if (Utility.hasConnection(this)) {
                        val map: MutableMap<String, Any> = HashMap()
                        map["user_id"] = "" + user_id
                        map["language"] =
                            "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                        presnter.resendLoginOtp(this@OtpVerification, map)
                    } else {
                        Utility.showToast(this, getString(R.string.no_network_message))
                    }
                }else if (cameFrom == "forgetPassword" && isNumberVerified){
                    if (Utility.hasConnection(this)) {
                        val map: MutableMap<String, Any> = HashMap()
                        map["email"] = "" + email
                        map["language"] =
                            "" + LanguagePreference(this@OtpVerification).languageType(this@OtpVerification)
                        presnter.resendForgetPassOtp(this@OtpVerification, map)
                    } else {
                        Utility.showToast(this, getString(R.string.no_network_message))
                    }
                }
                binding.pinView.text?.clear()
            }
        }
    }

    override fun onOtpSuccess(body: SignupModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AddBusiness::class.java)
            AppPreference.saveUserInfo(applicationContext, body.data!!)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
            startActivity(intent)
            finishAffinity()
            //val intent = Intent(this@OtpVerification, HomeActivity::class.java)
            //startActivity(intent)
        }else{
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResendSuccess(body: VerifyOtpModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResendLoginOtpSuccess(body: VerifyOtpModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onVerifyLoginOtpSuccess(body: SignupModel?) {
        val status = body!!.status
        if (status == 1){
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
            if (body.data?.business_setup_status == 0){
                val intent = Intent(this, AddBusiness::class.java)
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(intent)
                finishAffinity()

            }else if (body.data?.business_setup_status == 1){
                Session(this@OtpVerification).createLoginSession()
                val intent = Intent(this, HomeActivity::class.java)
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                //SessionManager.saveUser(_context,false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(intent)
                finishAffinity()
           }
        }else{
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onForgotPassOtpSuccess(body: VerifyOtpModel?) {
        val status = body!!.status
        if (status == 1){
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ResetPassword::class.java)
            intent.putExtra("user_id", user_id)
            startActivity(intent)
            finish()

        }else{
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResendForgotPassOtp(body: VerifyOtpModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this@OtpVerification, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getContext(): Context {
        return this
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d("token", token.toString())
            deviceToken = token.toString()
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@OtpVerification, LoginOptions::class.java)
        startActivity(intent)
        finish()
    }
}