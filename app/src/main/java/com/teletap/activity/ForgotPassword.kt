package com.teletap.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.ForgotPasswordActivityBinding
import com.teletap.model.ModelForgetPass
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.ForgetPassPresenter
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.IForgotPassView
import java.util.HashMap

class ForgotPassword : BaseActivity() , IForgotPassView{

    lateinit var binding : ForgotPasswordActivityBinding
    lateinit var presenterForgetPass : ForgetPassPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.forgot_password_activity)
        window.setBackgroundDrawableResource(R.drawable.screen_bg)

        presenterForgetPass = ForgetPassPresenter()
        presenterForgetPass.view = this


    }

    fun onclick(view: View) {
        when(view.id){
            R.id.btnSubmitFP ->{
                Utility.hideKeyboard(this@ForgotPassword)
                /*val intent = Intent(this, OtpVerification::class.java)
                startActivity(intent)*/
                if (TextUtils.isEmpty(binding.edEmailFP.text.toString())) {
                    Toast.makeText(this@ForgotPassword, "Please enter email", Toast.LENGTH_SHORT).show()
                    binding.edEmailFP.requestFocus()
                }else if (!Utility.isValidEmail(binding.edEmailFP.text.toString())) {
                    Toast.makeText(this@ForgotPassword, "Please enter valid email", Toast.LENGTH_SHORT).show()
                    binding.edEmailFP.requestFocus()
                }else{
                    if (Utility.hasConnection(this@ForgotPassword)) {
                        val map: MutableMap<String, Any> = HashMap()
                        map["email"] = binding.edEmailFP.text.toString().trim()
                        map["language"] =""+ LanguagePreference(this@ForgotPassword).languageType(this@ForgotPassword)
                        presenterForgetPass.forgetPassApi(this@ForgotPassword,map)
                    }else {
                        Utility.showToast(this@ForgotPassword, getString(R.string.no_network_message))
                    }
                }
            }
            R.id.backFP ->{
                finish()
            }
        }
    }

    override fun onSuccess(body: ModelForgetPass?) {
        val status = body!!.status
        if (status == 1) {
            Toast.makeText(this@ForgotPassword, body.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OtpVerification::class.java)
            intent.putExtra("cameFrom", "forgetPassword")
            intent.putExtra("user_id", body.data?.user_id)
            intent.putExtra("isNumberVerified", true)
            intent.putExtra("email", binding.edEmailFP.text.toString().trim())
            //body.data?.let { intent.putExtra("mobile", it.mobile) }
            startActivity(intent)
            finish()
        }
    }

    override fun getContext(): Context {
        return this
    }
}
