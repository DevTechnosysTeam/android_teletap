package com.teletap.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.adapter.AdapterTransitionHistoryCD
import com.teletap.databinding.DetailsContactActivityBinding
import com.teletap.dialogFragment.ContactOptionsDialog
import com.teletap.model.ModelContactList
import com.teletap.model.ModelDetailsContact
import com.teletap.model.SignupModel
import com.teletap.model.SimpleModel
import com.teletap.presenterClasses.DetailsContactPresenter
import com.teletap.utilities.Utility
import com.teletap.view.IContactDetailView
import java.util.HashMap

class DetailsContact : BaseActivity(), IContactDetailView, AdapterTransitionHistoryCD.OnItemClickListener, ContactOptionsDialog.BottomOptionsListener {
    lateinit var binding : DetailsContactActivityBinding
    lateinit var adapter : AdapterTransitionHistoryCD
    lateinit var userInfo: SignupModel.DataBean

    private lateinit var optionsDialog :ContactOptionsDialog

    lateinit var presenterDetails : DetailsContactPresenter
    private var contactId: Int = 0

    private var modelList: ArrayList<ModelDetailsContact.DataBean.TransactionHistoryBean> = ArrayList<ModelDetailsContact.DataBean.TransactionHistoryBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.details_contact_activity)
        presenterDetails = DetailsContactPresenter()
        presenterDetails.view = this

        if(intent!=null)
            contactId = intent.getIntExtra("contactId", 0)

        try {
            userInfo = AppPreference.getUserInfo(this@DetailsContact)
            Log.e("userInfo", userInfo.token.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@DetailsContact)
        adapter = AdapterTransitionHistoryCD(this@DetailsContact, /*modelList,*/ this);
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        getDetailsApi()

    }

    private fun getDetailsApi(){
        if (contactId!=0){
            if (Utility.hasConnection(this@DetailsContact)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["contact_id"] = ""+contactId
                presenterDetails.contactDetailsApi(this@DetailsContact, map)
            } else {
                Utility.showToast(this@DetailsContact, getString(R.string.no_network_message))
            }
        }
    }

    override fun onChatItemClick(view: View?, index: Int) {

    }


    fun onclick(view: View) {
        when(view.id){
            R.id.imgMoreOptions -> {
                optionsDialog = ContactOptionsDialog()
                optionsDialog.show(supportFragmentManager, "ContactOptionsDialog")
            }
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.btnCreateBill -> {

            }
        }
    }

    override fun onEdit(view: View?) {
        optionsDialog.dismiss()
        val intent = Intent(this@DetailsContact, AddContact::class.java)
        intent.putExtra("function", "edit")
        intent.putExtra("contactId",contactId)
        startActivityForResult(intent, 102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 102){
            modelList.clear()
            getDetailsApi()
        }
    }

    override fun onSuccessDetails(body: ModelDetailsContact?) {
        val status = body!!.status
        if (status == 1) {
            binding.contactName.text = body.data?.name
            binding.tvMobileNumber.text = body.data?.country_code + body.data?.mobile
            binding.tvEmail.text = body.data?.email

            body.data?.transaction_history.let {
                if (it != null) {
                    modelList.addAll(it)
                }
            }

            adapter.notifyDataSetChanged()
            if (modelList.size > 0){
                binding.recyclerView.visibility = View.VISIBLE
                binding.noHistoryFound.visibility = View.GONE
            }else{

                binding.recyclerView.visibility = View.GONE
                binding.noHistoryFound.visibility = View.VISIBLE
            }

        }else{
            Utility.shortToast(this@DetailsContact, body.message)
        }
    }

    override fun onDelete(view: View?) {
        if (contactId!=0){
            if (Utility.hasConnection(this@DetailsContact)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["contact_id"] = ""+contactId
                presenterDetails.deleteContactApi(this@DetailsContact, map)
            } else {
                Utility.showToast(this@DetailsContact, getString(R.string.no_network_message))
            }
        }
        optionsDialog.dismiss()
    }

    override fun onDeleteSuccess(body: SimpleModel?) {
        val status = body!!.status
        if (status == 1) {
            Utility.shortToast(this@DetailsContact, body.message)
            finish()
        }else{
            Utility.shortToast(this@DetailsContact, body.message)
        }
    }

    override fun getContext(): Context {
        return this
    }
}