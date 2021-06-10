package com.teletap.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
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
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.adapter.Country_code_Adapter
import com.teletap.databinding.SignUpActivityBinding
import com.teletap.model.CountryModel
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.SignupPresenter
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.CodePicker
import com.teletap.view.ISignUpView
import kotlinx.android.synthetic.main.sign_up_activity.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class SignUp : BaseActivity(), ISignUpView {

    lateinit var binding : SignUpActivityBinding
    lateinit var  presnter: SignupPresenter
    lateinit var codeModels: List<CountryModel>
    var adapter: Country_code_Adapter? = null
    lateinit var dialog1: Dialog
    var layoutManager: RecyclerView.LayoutManager? = null
    lateinit var recyclerView: RecyclerView

    private var deviceToken : String = ""

    private var cameFrom : String = ""
    private var googleId : String = ""
    private var firstName : String = ""
    private var lastName : String = ""
    private var email : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.sign_up_activity)
        presnter = SignupPresenter()
        presnter.view = this
        getFcmToken()
        val str = getString(R.string.i_accept)+" "+"<b><font color='#0F4C75'> <a href='https://teletap.devtechnosys.info/terms' >Terms and conditions</a></font></b>"
        val s:Spannable = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT) as Spannable
        for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
            s.setSpan(object : UnderlineSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.isUnderlineText = false
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0)
        }
        //binding.textCheckbox.text = Html.fromHtml(s.toString(), Html.FROM_HTML_MODE_COMPACT)
        binding.textCheckbox.text = s
        binding.textCheckbox.movementMethod = LinkMovementMethod.getInstance();

        if(intent!=null){
            cameFrom = intent.getStringExtra("cameFrom").toString()
            if (cameFrom == "googleLogin"){
                googleId = intent.getStringExtra("googleId").toString()
                firstName = intent.getStringExtra("firstName").toString()
                lastName = intent.getStringExtra("lastName").toString()
                email = intent.getStringExtra("email").toString()
            }
        }

        if (cameFrom == "googleLogin"){
            if (email == "") {
                binding.edEmail.isFocusable = true
            }else{
                binding.edEmail.setText(email)
                binding.edEmail.isFocusable = false
            }

            binding.edFirstName.setText(firstName)
            binding.edLastName.setText(lastName)

            binding.password.visibility = View.GONE
            binding.confirmPassword.visibility = View.GONE
        }


    }

    fun onClick(view: View) {
        when(view.id){
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.btnSignUp -> {
                Utility.hideKeyboard(this)
                if (cameFrom == "login")
                    validationCheck()
                else if (cameFrom == "googleLogin")
                    validationGoogleCheck()
                /*val intent = Intent(this@SignUp, OtpVerification::class.java)
                startActivity(intent)*/
            }
            R.id.edCountryCode -> {
                Utility.hideKeyboard(this@SignUp)
                dialogCountryCode()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (cameFrom == "login"){
            finish()
        }else if (cameFrom == "googleLogin") {
            val intent = Intent(this@SignUp, LoginOptions::class.java)
            startActivity(intent)
            finish()
        }
    }

    /*Password Mismatch! Please enter the same password in both the fields
    Mobile number must be 7 - 15 digits*/

    private fun validationGoogleCheck(){
        if (TextUtils.isEmpty(binding.edFirstName.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter first name", Toast.LENGTH_SHORT).show()
            binding.edFirstName.requestFocus()
        }else if (TextUtils.isEmpty(binding.edLastName.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter last name", Toast.LENGTH_SHORT).show()
            binding.edLastName.requestFocus()
        }else if (TextUtils.isEmpty(binding.edEmail.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter email", Toast.LENGTH_SHORT).show()
            binding.edEmail.requestFocus()
        }else if (!Utility.isValidEmail(binding.edEmail.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter valid email", Toast.LENGTH_SHORT).show()
            binding.edEmail.requestFocus()
        }else if (TextUtils.isEmpty(binding.edCountryCode.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please select country code", Toast.LENGTH_SHORT).show()
            binding.edCountryCode.requestFocus()
        }else if (TextUtils.isEmpty(binding.edMobileNo.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            binding.edMobileNo.requestFocus()
        }else if (binding.edMobileNo.text?.length!! < 7) {
            Toast.makeText(this@SignUp, "Mobile number must be 7 - 15 digits", Toast.LENGTH_SHORT).show()
            binding.edMobileNo.requestFocus()
        }else if(!binding.checkbox.isChecked){
            Toast.makeText(this@SignUp, "Please except Terms and conditions", Toast.LENGTH_SHORT).show()
        }else{
            callGoogleApi()
        }
    }

    private fun callGoogleApi() {
        if (Utility.hasConnection(this@SignUp)) {
            val map: MutableMap<String, Any> = HashMap()
            map["email"] = "" + email
            map["country_code"] = binding.edCountryCode.text.toString().trim()
            map["mobile"] = binding.edMobileNo.text.toString().trim()
            map["google_token"] = "" + googleId
            map["first_name"] = "" + firstName
            map["last_name"] = "" + lastName
            map["device_type"] = "" + "1"
            map["device_token"] = "" + deviceToken

            presnter.googleLogin(this@SignUp, map)
        } else {
            Utility.showToast(this@SignUp, getString(R.string.no_network_message))
        }
    }

    private fun validationCheck() {
        Log.e("country", binding.edCountryCode.text.toString().trim())

        if (TextUtils.isEmpty(binding.edFirstName.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter first name", Toast.LENGTH_SHORT).show()
            binding.edFirstName.requestFocus()
        }else if (TextUtils.isEmpty(binding.edLastName.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter last name", Toast.LENGTH_SHORT).show()
            binding.edLastName.requestFocus()
        }else if (TextUtils.isEmpty(binding.edEmail.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter email", Toast.LENGTH_SHORT).show()
            binding.edEmail.requestFocus()
        }else if (!Utility.isValidEmail(binding.edEmail.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter valid email", Toast.LENGTH_SHORT).show()
            binding.edEmail.requestFocus()
        }else if (TextUtils.isEmpty(binding.edCountryCode.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please select country code", Toast.LENGTH_SHORT).show()
            binding.edCountryCode.requestFocus()
        }else if (TextUtils.isEmpty(binding.edMobileNo.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            binding.edMobileNo.requestFocus()
        }else if (binding.edMobileNo.text?.length!! < 7) {
            Toast.makeText(this@SignUp, "Mobile number must be 7 - 15 digits", Toast.LENGTH_SHORT).show()
            binding.edMobileNo.requestFocus()
        }else if (TextUtils.isEmpty(binding.edPassword.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter password", Toast.LENGTH_SHORT).show()
            binding.edPassword.requestFocus()
        }else if (binding.edPassword.text?.length!! < 7) {
            Toast.makeText(
                this@SignUp,
                "Password must contain at least 7 characters",
                Toast.LENGTH_SHORT
            ).show()
            binding.edPassword.requestFocus()
        }else if (TextUtils.isEmpty(binding.edConfirmPassword.text.toString().trim())) {
            Toast.makeText(this@SignUp, "Please enter confirm password", Toast.LENGTH_SHORT).show()
            binding.edConfirmPassword.requestFocus()
        }else if(!Utility.isPasswordSame(
                binding.edPassword.text.toString(),
                binding.edConfirmPassword.text.toString()
            )){
            Toast.makeText(this@SignUp, "Password Mismatch! Please enter the same password in both the fields.", Toast.LENGTH_SHORT).show()
        }else if(!binding.checkbox.isChecked){
            Toast.makeText(this@SignUp, "Please except Terms and conditions", Toast.LENGTH_SHORT).show()
        }else {
            doRegister()
        }
    }

    private fun doRegister() {
        if (Utility.hasConnection(this@SignUp)) {

            val map: MutableMap<String, Any> = HashMap()
            map["email"] = binding.edEmail.text.toString().trim()
            map["country_code"] = binding.edCountryCode.text.toString().trim()
            map["mobile"] = binding.edMobileNo.text.toString().trim()
            map["password"] = binding.edPassword.text.toString().trim()
            map["first_name"] = binding.edFirstName.text.toString().trim()
            map["last_name"] = binding.edLastName.text.toString().trim()
            map["device_type"] = "" + "1"
            map["device_token"] = "" + deviceToken
            map["language"] =""+ LanguagePreference(this@SignUp).languageType(this@SignUp)
            presnter.register(this@SignUp, map)
        } else {
            Utility.showToast(this@SignUp, getString(R.string.no_network_message))
        }
    }

    override fun onSuccess(body: SignupModel?) {

        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@SignUp, body.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OtpVerification::class.java)
            intent.putExtra("cameFrom", "signUp")
            body.data?.let { intent.putExtra("user_id", it.user_id) }
            intent.putExtra("isNumberVerified", false)
            //body.data?.let { intent.putExtra("countryCode", it.country_code) }
            //body.data?.let { intent.putExtra("mobile", it.mobile) }
            startActivity(intent)
            finish()

        }else if (status==0){
            Utility.showToast(this, body.message)

        }
    }

    override fun onGoogleLogin(body: SignupModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@SignUp, body.message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OtpVerification::class.java)
            intent.putExtra("cameFrom", "googleLogin")
            body.data?.let { intent.putExtra("user_id", it.id) }
            intent.putExtra("isNumberVerified", false)
            startActivity(intent)
            finish()
        }else if (status==0){
            Utility.showToast(this, body.message)

        }
    }


    override fun getContext(): Context {
        return this
    }

    private fun dialogCountryCode() {
        dialog1 = Dialog(this@SignUp)
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1.setContentView(R.layout.dialog_country_code_picker_layout)
        recyclerView = dialog1.findViewById(R.id.ccRecycler)
        dialog1.window!!.setLayout(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT
        );
        val etSearch : EditText = dialog1.findViewById(R.id.etSearch)
        val ivClose: ImageView = dialog1.findViewById(R.id.ivClose)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        codeModels = ArrayList<CountryModel>()

        try {
            val jsonArray = JSONArray(ConstantValues.COUNTRY_CODE_)
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val name = jsonObject.optString("name")
                val code = jsonObject.optString("dial_code")
                (codeModels as ArrayList<CountryModel>).add(CountryModel(name, code))
            }
            adapter = Country_code_Adapter(this@SignUp, codeModels, codePicker)
            recyclerView.adapter = adapter
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ivClose.setOnClickListener { dialog1.dismiss() }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.trim().toString(), codeModels.toMutableList())
            }
        })

        dialog1.show()
    }

    private fun filter(toString: String, codeModels12: MutableList<CountryModel?>) {
        var codeModels_12: MutableList<CountryModel?> = codeModels12
        codeModels_12 = ArrayList<CountryModel?>()
        for (item in codeModels) {
            if (item.countryName.toLowerCase(Locale.ROOT).contains(
                    toString.toLowerCase(Locale.ROOT).toUpperCase(
                        Locale.ROOT
                    )
                )
                || item.countryName.toUpperCase(Locale.ROOT).contains(toString.toUpperCase(Locale.ROOT))) {
                codeModels_12.add(item)
            }
        }
        adapter!!.filterList(codeModels_12)
    }

    var codePicker: CodePicker = object : CodePicker {
        override fun selectedCountryCode(country_code: String?) {
            val country_code_:String = country_code+""
            binding.edCountryCode.setText(country_code_)
            dialog1.dismiss()
            Utility.hideKeyboard(this@SignUp)
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

}