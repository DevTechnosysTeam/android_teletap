package com.teletap.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.adapter.AdapterContactList
import com.teletap.databinding.ContactsListActivityBinding
import com.teletap.model.ModelContactList
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.ContactListPresenter
import com.teletap.utilities.Utility
import com.teletap.view.IContactListView
import kotlinx.android.synthetic.main.contacts_list_activity.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap
import kotlin.collections.set

class ContactsListActivity : BaseActivity(), IContactListView, AdapterContactList.OnItemClickListener, View.OnClickListener {

    lateinit var adapter : AdapterContactList
    private lateinit var binding : ContactsListActivityBinding
    lateinit var presenterContactList : ContactListPresenter
    lateinit var userInfo: SignupModel.DataBean
    private var modelList: ArrayList<ModelContactList.DataBean> = ArrayList<ModelContactList.DataBean>()

    private var search:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.contacts_list_activity)
        presenterContactList = ContactListPresenter()
        presenterContactList.view = this

        try {
            userInfo = AppPreference.getUserInfo(this@ContactsListActivity)
            Log.e("userInfo", userInfo.token.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        binding.toolBar.imgBack.setOnClickListener(this)
        binding.fabAddContact.setOnClickListener(this)
        binding.imgSearch.setOnClickListener(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this@ContactsListActivity)
        adapter = AdapterContactList(this@ContactsListActivity, modelList, this);
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        binding.edSearchAC.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // Perform action on key press
                Utility.hideKeyboard(this@ContactsListActivity)
                search = binding.edSearchAC.text.toString().trim()
                //if (search != "") {
                    modelList.clear()
                    adapter.notifyDataSetChanged()
                    getContactListApi()
                //}
                return@OnKeyListener true
            }
            false
        })

    }

    override fun onResume() {
        super.onResume()
        if(modelList.size > 0)
        modelList.clear()

        getContactListApi()
    }

    private fun getContactListApi() {
        if (Utility.hasConnection(this@ContactsListActivity)) {
            val map: MutableMap<String, Any> = HashMap()
            map["token"] = ""+userInfo.token!!
            map["search"] = ""+search
            presenterContactList.getContactListApi(this@ContactsListActivity, map)
        } else {
            Utility.showToast(this@ContactsListActivity, getString(R.string.no_network_message))
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.fabAddContact -> {
                val intent = Intent(this, AddContact::class.java)
                intent.putExtra("function", "add")
                startActivity(intent)
            }
            R.id.imgSearch -> {
                if (binding.rltvSearch.visibility == View.VISIBLE) {
                    binding.rltvSearch.visibility = View.GONE
                } else {
                    binding.rltvSearch.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun onSuccess(body: ModelContactList?) {
        val status = body!!.status
        if (status == 1) {
            body.data?.let { modelList.addAll(it) }
            adapter.notifyDataSetChanged()
            if (modelList.size > 0){
                binding.recyclerView.visibility = View.VISIBLE
                binding.noContactFound.visibility = View.GONE
            }else{
                binding.recyclerView.visibility = View.GONE
                binding.noContactFound.visibility = View.VISIBLE
            }

        }else{
            binding.recyclerView.visibility = View.GONE
            binding.noContactFound.visibility = View.VISIBLE
        }

    }

    override fun getContext(): Context {
        return this
    }

    override fun onContactItemClick(view: View?, index: Int, modelBean: ModelContactList.DataBean) {
        val intent = Intent(this@ContactsListActivity, DetailsContact::class.java)
        intent.putExtra("contactId", modelBean.id)
        startActivity(intent)
    }
}