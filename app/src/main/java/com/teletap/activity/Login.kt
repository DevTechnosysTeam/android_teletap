package com.teletap.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.Session
import com.teletap.adapter.Country_code_Adapter
import com.teletap.databinding.LoginActivityBinding
import com.teletap.model.CountryModel
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.LoginPresenter
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.CodePicker
import com.teletap.view.ILoginView
import kotlinx.android.synthetic.main.sign_up_activity.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Login : BaseActivity(), ILoginView {
    lateinit var binding : LoginActivityBinding
    lateinit var presenterLogin : LoginPresenter
    private var isEmailLogin : Boolean = true
    private var deviceToken : String = ""
    lateinit var codeModels: List<CountryModel>
    var adapter: Country_code_Adapter? = null
    lateinit var dialog1: Dialog
    var layoutManager: RecyclerView.LayoutManager? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        presenterLogin = LoginPresenter()
        presenterLogin.view = this
        getFcmToken()
        binding.edEmailMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.trim().matches("[0-9]+".toRegex())){
                        binding.countryCodeLogin.visibility = View.VISIBLE
                        binding.passwordLogin.visibility = View.GONE
                        isEmailLogin = false
                    }else{
                        binding.countryCodeLogin.visibility = View.GONE
                        binding.passwordLogin.visibility = View.VISIBLE
                        isEmailLogin = true
                    }

                }
            }
        })
    }

    fun onClickLogin(view: View) {
        when(view.id){
            R.id.forgotPassword ->{
                val intent = Intent(this, ForgotPassword::class.java)
                startActivity(intent)
            }
            R.id.signUp ->{
                val intent = Intent(this, SignUp::class.java)
                intent.putExtra("cameFrom", "login")
                startActivity(intent)
                finish()
            }
            R.id.btnLogin ->{
                Utility.hideKeyboard(this@Login)
                /*val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)*/
                if (isEmailLogin){
                    checkEmailValidation()
                }else{
                    checkMobileValidation()
                }
            }
            R.id.edCountryCodeLogin ->{
                Utility.hideKeyboard(this@Login)
                dialogCountryCode()
            }
            R.id.imgBackLogin ->{
                onBackPressed()
            }
        }
    }


    private fun checkMobileValidation() {
        if (TextUtils.isEmpty(binding.edCountryCodeLogin.text.toString())) {
            Toast.makeText(this@Login, "Please select country code", Toast.LENGTH_SHORT).show()
            binding.edCountryCodeLogin.requestFocus()
        }else if (TextUtils.isEmpty(binding.edEmailMobile.text.toString())) {
            Toast.makeText(this@Login, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            binding.edEmailMobile.requestFocus()
        }else if (binding.edEmailMobile.text?.length!! < 7) {
            Toast.makeText(this@Login, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            binding.edEmailMobile.requestFocus()
        }else{
            mobileLoginApi()
        }

    }

    private fun mobileLoginApi() {
        if (Utility.hasConnection(this@Login)) {
            val map: MutableMap<String, Any> = HashMap()
            map["mobile"] = binding.edEmailMobile.text.toString().trim()
            map["country_code"] = binding.edCountryCodeLogin.text.toString().trim()
            //map["device_type"] = "" + "1"
            //map["device_token"] = "" + deviceToken
            map["language"] =""+ LanguagePreference(this@Login).languageType(this@Login)
            presenterLogin.numberLogin(this@Login, map)
        } else {
            Utility.showToast(this@Login, getString(R.string.no_network_message))
        }
    }

    private fun checkEmailValidation() {
        if (TextUtils.isEmpty(binding.edEmailMobile.text.toString())) {
            Toast.makeText(this@Login, "Please enter email", Toast.LENGTH_SHORT).show()
            binding.edEmailMobile.requestFocus()
        }else if (!Utility.isValidEmail(binding.edEmailMobile.text.toString())) {
            Toast.makeText(this@Login, "Please enter valid email", Toast.LENGTH_SHORT).show()
            binding.edEmailMobile.requestFocus()
        }else if (TextUtils.isEmpty(binding.edPasswordLogin.text.toString())) {
            Toast.makeText(this@Login, "Please enter password", Toast.LENGTH_SHORT).show()
            binding.edPasswordLogin.requestFocus()
        }else{
            emailLoginApi()
        }
    }

    private fun emailLoginApi() {
        if (Utility.hasConnection(this@Login)) {
            val map: MutableMap<String, Any> = HashMap()
            map["email"] = binding.edEmailMobile.text.toString().trim()
            map["password"] = binding.edPasswordLogin.text.toString().trim()
            map["device_type"] = "" + "1"
            map["device_token"] = "" + deviceToken
            map["language"] =""+ LanguagePreference(this@Login).languageType(this@Login)
            presenterLogin.emailLogin(this@Login, map)
        } else {
            Utility.showToast(this@Login, getString(R.string.no_network_message))
        }
    }

    override fun onSuccessEmail(body: SignupModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@Login, body.message, Toast.LENGTH_SHORT).show()
            if (body.data?.sms_verified_status == 0){
                val intent = Intent(this, OtpVerification::class.java)
                intent.putExtra("cameFrom", "login")
                body.data?.let { intent.putExtra("user_id", it.id) }
                intent.putExtra("isNumberVerified", false)
                startActivity(intent)
                finish()

            }else if (body.data?.sms_verified_status == 1 && body.data?.business_setup_status == 0) {
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                val intent = Intent(this, AddBusiness::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(intent)
                finishAffinity()
                /*Session(this@Login).createLoginSession()
                val intent = Intent(this, HomeActivity::class.java)
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                //SessionManager.saveUser(_context,false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(intent)
                finishAffinity()*/

            }else if (body.data?.sms_verified_status == 1 && body.data?.business_setup_status == 1){
                Session(this@Login).createLoginSession()
                val intent = Intent(this, HomeActivity::class.java)
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                //SessionManager.saveUser(_context,false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(intent)
                finishAffinity()
            }

        }else if (status==0){
            Utility.showToast(this, body.message)

        }
    }

    override fun onNumberLogin(body: SignupModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@Login, body.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OtpVerification::class.java)
            intent.putExtra("cameFrom", "login")
            body.data?.let { intent.putExtra("user_id", it.user_id) }
            if (body.data?.sms_verified_status == 1){
                intent.putExtra("isNumberVerified", true)
            }else if (body.data?.sms_verified_status == 0){
                intent.putExtra("isNumberVerified", false)
            }
            //body.data?.let { intent.putExtra("countryCode", it.country_code) }
            //body.data?.let { intent.putExtra("mobile", it.mobile) }
            startActivity(intent)
            finishAffinity()

        }else if (status==0){
            Utility.showToast(this, body.message)

        }
    }

    override fun getContext(): Context {
        return this
    }

    private fun dialogCountryCode() {
        dialog1 = Dialog(this@Login)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.dialog_country_code_picker_layout)
        recyclerView = dialog1.findViewById(R.id.ccRecycler)
        dialog1.getWindow()!!.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        val etSearch : EditText = dialog1.findViewById(R.id.etSearch)
        val ivClose: ImageView = dialog1.findViewById(R.id.ivClose)
        layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)
        codeModels = ArrayList<CountryModel>()

        try {
            val jsonArray = JSONArray(ConstantValues.COUNTRY_CODE_)
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val name = jsonObject.optString("name")
                val code = jsonObject.optString("dial_code")
                (codeModels as ArrayList<CountryModel>).add(CountryModel(name, code))
            }
            adapter = Country_code_Adapter(this@Login, codeModels, codePicker)
            recyclerView.adapter = adapter
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ivClose.setOnClickListener { dialog1.dismiss() }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString(), codeModels.toMutableList())
            }
        })

        dialog1.show()
    }

    private fun filter(toString: String, codeModels12: MutableList<CountryModel?>) {
        var codeModels_12: MutableList<CountryModel?> = codeModels12
        codeModels_12 = ArrayList<CountryModel?>()
        for (item in codeModels) {
            if (item.countryName.toLowerCase(Locale.ROOT).contains(toString.toLowerCase(Locale.ROOT).toUpperCase(
                    Locale.ROOT))
                || item.countryName.toUpperCase(Locale.ROOT).contains(toString.toUpperCase(Locale.ROOT))) {
                codeModels_12.add(item)
            }
        }
        adapter!!.filterList(codeModels_12)
    }

    var codePicker: CodePicker = object : CodePicker {
        override fun selectedCountryCode(country_code: String?) {
            val country_code_:String = country_code.toString()
            binding.edCountryCodeLogin.setText(country_code_)
            dialog1.dismiss()
            Utility.hideKeyboard(this@Login)
//            HideKeyboard.setupUI(binding.mainLayout,this@SignupActivity)
        }
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
        val intent = Intent(this@Login, LoginOptions::class.java)
        startActivity(intent)
        finish()
    }
}