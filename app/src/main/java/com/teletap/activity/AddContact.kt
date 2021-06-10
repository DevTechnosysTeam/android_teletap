package com.teletap.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
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
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.adapter.Country_code_Adapter
import com.teletap.databinding.AddContactActivityBinding
import com.teletap.model.CountryModel
import com.teletap.model.ModelDetailsContact
import com.teletap.model.SignupModel
import com.teletap.model.SimpleModel
import com.teletap.presenterClasses.AddContactPresenter
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.Utility
import com.teletap.view.CodePicker
import com.teletap.view.IAddContactView
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class AddContact : BaseActivity(), IAddContactView, View.OnClickListener {
    lateinit var binding : AddContactActivityBinding
    lateinit var presenterAddContact : AddContactPresenter
    lateinit var userInfo: SignupModel.DataBean

    lateinit var codeModels: List<CountryModel>
    var adapter: Country_code_Adapter? = null
    lateinit var dialog1: Dialog
    var layoutManager: RecyclerView.LayoutManager? = null
    lateinit var recyclerView: RecyclerView

    private var function: String = ""
    private var contactId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.add_contact_activity)
        presenterAddContact = AddContactPresenter()
        presenterAddContact.view = this

        try {
            userInfo = AppPreference.getUserInfo(this@AddContact)
            Log.e("userInfo", userInfo.token.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        if(intent!=null){
            function = intent.getStringExtra("function").toString()
            if (function == "edit"){
                contactId = intent.getIntExtra("contactId", 0)
                binding.toolBarAC.title.text = getString(R.string.edit_contact)
            }else{
                binding.toolBarAC.title.text = getString(R.string.add_contact)
            }
        }

        //binding.toolBarAC.title.text = getString(R.string.add_contact)
        binding.toolBarAC.imgBack.setOnClickListener { onBackPressed() }

        binding.edNameAC.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        binding.edCountryCodeAC.setOnClickListener(this)
        binding.btnSaveAC.setOnClickListener(this)

        if (function == "edit"){
            getContactDetailsApi()
        }

    }

    private fun getContactDetailsApi() {
        if (contactId!=0){
            if (Utility.hasConnection(this@AddContact)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["contact_id"] = ""+contactId
                presenterAddContact.contactDetailsApi(this@AddContact, map)
            } else {
                Utility.showToast(this@AddContact, getString(R.string.no_network_message))
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.edCountryCodeAC -> {
                Utility.hideKeyboard(this@AddContact)
                dialogCountryCode()
            }
            R.id.btnSaveAC -> {
                Utility.hideKeyboard(this@AddContact)
                checkValidations()
            }
        }
    }

    private fun checkValidations() {
        if (TextUtils.isEmpty(binding.edNameAC.text.toString().trim())) {
            Toast.makeText(this@AddContact, "Please enter name", Toast.LENGTH_SHORT).show()
            binding.edNameAC.requestFocus()
        }else if (TextUtils.isEmpty(binding.edEmailAC.text.toString().trim())) {
            Toast.makeText(this@AddContact, "Please enter email", Toast.LENGTH_SHORT).show()
            binding.edEmailAC.requestFocus()
        }else if (!Utility.isValidEmail(binding.edEmailAC.text.toString().trim())) {
            Toast.makeText(this@AddContact, "Please enter valid email", Toast.LENGTH_SHORT).show()
            binding.edEmailAC.requestFocus()
        }else if (TextUtils.isEmpty(binding.edCountryCodeAC.text.toString().trim())) {
            Toast.makeText(this@AddContact, "Please select country code", Toast.LENGTH_SHORT).show()
            binding.edCountryCodeAC.requestFocus()
        }else if (TextUtils.isEmpty(binding.edMobileNoAC.text.toString().trim())) {
            Toast.makeText(this@AddContact, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            binding.edMobileNoAC.requestFocus()
        }else if (binding.edMobileNoAC.text?.length!! < 7) {
            Toast.makeText(this@AddContact, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            binding.edMobileNoAC.requestFocus()
        }else{
            if (function == "add")
                addContactApi()
            else
                updateContactApi()
        }
    }



    private fun dialogCountryCode() {
        dialog1 = Dialog(this@AddContact)
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
            adapter = Country_code_Adapter(this@AddContact, codeModels, codePicker)
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
            binding.edCountryCodeAC.setText(country_code_)
            dialog1.dismiss()
            Utility.hideKeyboard(this@AddContact)
        }
    }

    private fun addContactApi(){
        if (Utility.hasConnection(this@AddContact)) {
            val map: MutableMap<String, Any> = HashMap()
            map["email"] = binding.edEmailAC.text.toString().trim()
            map["country_code"] = binding.edCountryCodeAC.text.toString().trim()
            map["mobile"] = binding.edMobileNoAC.text.toString().trim()
            map["token"] = ""+userInfo.token!!
            map["name"] = binding.edNameAC.text.toString().trim()
            presenterAddContact.addContactApi(this@AddContact, map)
        } else {
            Utility.showToast(this@AddContact, getString(R.string.no_network_message))
        }
    }

    override fun onSuccess(body: SimpleModel?) {
        val status = body!!.status
        if (status == 1) {
            Utility.shortToast(this@AddContact, body.message)
            finish()
        }else{
            Utility.shortToast(this@AddContact, body.message)
        }
    }

    private fun updateContactApi() {
        if (Utility.hasConnection(this@AddContact)) {
            val map: MutableMap<String, Any> = HashMap()
            map["email"] = binding.edEmailAC.text.toString().trim()
            map["country_code"] = binding.edCountryCodeAC.text.toString().trim()
            map["mobile"] = binding.edMobileNoAC.text.toString().trim()
            map["token"] = ""+userInfo.token!!
            map["name"] = binding.edNameAC.text.toString().trim()
            map["contact_id"] = ""+ contactId
            presenterAddContact.editContactApi(this@AddContact, map)
        } else {
            Utility.showToast(this@AddContact, getString(R.string.no_network_message))
        }
    }

    override fun onEditSuccess(body: SimpleModel?) {
        val status = body!!.status
        if (status == 1) {
            Utility.shortToast(this@AddContact, body.message)
            setResult(RESULT_OK)
            finish()

        }else{
            Utility.shortToast(this@AddContact, body.message)
        }
    }

    override fun onSuccessDetails(body: ModelDetailsContact?) {
        val status = body!!.status
        if (status == 1) {
            binding.edNameAC.setText(body.data?.name)
            binding.edCountryCodeAC.setText(body.data?.country_code)
            binding.edMobileNoAC.setText(body.data?.mobile)
            binding.edEmailAC.setText(body.data?.email)
        }else{
            Utility.shortToast(this@AddContact, body.message)
        }
    }

    override fun getContext(): Context {
        return this
    }
}