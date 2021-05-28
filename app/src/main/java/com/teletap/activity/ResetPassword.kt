package com.teletap.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.ResetPasswordActivityBinding
import com.teletap.model.SimpleModel
import com.teletap.presenterClasses.ResetPassPresenter
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.IResetPassView
import kotlinx.android.synthetic.main.sign_up_activity.*
import java.util.HashMap

class ResetPassword : BaseActivity(), IResetPassView{
    lateinit var binding : ResetPasswordActivityBinding
    lateinit var presenterReset : ResetPassPresenter

    private var user_id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.reset_password_activity)
        presenterReset = ResetPassPresenter()
        presenterReset.view = this

        if (intent!=null)
            user_id = intent.getIntExtra("user_id", 0)

    }

    fun onclick(view: View) {
        when(view.id){
            R.id.backRP ->{
                onBackPressed()
            }
            R.id.btnSubmitRP ->{
                Utility.hideKeyboard(this@ResetPassword)
                validations()
            }

        }
    }

    private fun validations() {
        if (TextUtils.isEmpty(binding.edNewPassRP.text.toString())) {
            Toast.makeText(this@ResetPassword, "Please enter password", Toast.LENGTH_SHORT).show()
            binding.edNewPassRP.requestFocus()
        }else if (binding.edNewPassRP.text?.length!! < 7) {
            Toast.makeText(this@ResetPassword, "Password must contain at least 7 characters", Toast.LENGTH_SHORT).show()
            binding.edNewPassRP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edConfirmPassRP.text.toString())) {
            Toast.makeText(this@ResetPassword, "Please enter confirm password", Toast.LENGTH_SHORT).show()
            binding.edConfirmPassRP.requestFocus()
        }else if(!Utility.isPasswordSame(binding.edNewPassRP.text.toString(), binding.edConfirmPassRP.text.toString())){
            Toast.makeText(this@ResetPassword, "Confirm Password does not match", Toast.LENGTH_SHORT).show()
        }else{
            if (Utility.hasConnection(this@ResetPassword)) {
                val map: MutableMap<String, Any> = HashMap()

                map["newPassword"] = binding.edNewPassRP.text.toString().trim()
                map["user_id"] = ""+user_id
                map["language"] =""+ LanguagePreference(this@ResetPassword).languageType(this@ResetPassword)
                presenterReset.resetPassApi(this@ResetPassword, map)
            } else {
                Utility.showToast(this@ResetPassword, getString(R.string.no_network_message))
            }
        }
    }

    override fun onSuccess(body: SimpleModel?) {
        val status = body!!.status
        if (status == 1){
            Toast.makeText(this@ResetPassword, body.message, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()

        }else{
            Toast.makeText(this@ResetPassword, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getContext(): Context {
        return this
    }
}