package com.teletap.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.teletap.AppPreference
import com.teletap.R
import com.teletap.activity.DetailPayout
import com.teletap.adapter.AdapterEarnings
import com.teletap.adapter.AdapterPayOuts
import com.teletap.databinding.PayoutFragmentLayoutBinding
import com.teletap.model.ModelEarningsHome
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.EarningHomePresenter
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.SharedPreferenceUtility
import com.teletap.utilities.Utility
import com.teletap.view.IHomeEarningsView
import java.util.HashMap

class PayOutFragment : BaseFragment(), AdapterPayOuts.OnItemClickListener , IHomeEarningsView {

    lateinit var binding : PayoutFragmentLayoutBinding
    lateinit var adapter : AdapterPayOuts
    lateinit var presenterEarning : EarningHomePresenter

    lateinit var userInfo: SignupModel.DataBean
    private var token : String = ""
    private var modelList: ArrayList<ModelEarningsHome.DataBean.EarningDataBean> = ArrayList<ModelEarningsHome.DataBean.EarningDataBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater, R.layout.payout_fragment_layout, container, false)
        presenterEarning = EarningHomePresenter()
        presenterEarning.view = this
        try {
            userInfo = AppPreference.getUserInfo(context)
            Log.e("userInfo", userInfo.id.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }
        token = userInfo.token.toString()

        binding.recyclerPayout.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { AdapterPayOuts(it, modelList, this) }!!
        binding.recyclerPayout.adapter = adapter
        adapter.notifyDataSetChanged()

        getPayoutListApi()


        return binding.root
    }

    private fun getPayoutListApi(){
        if (userInfo.token!=null){
            if (Utility.hasConnection(activity)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["start_date_filter"] = ""
                map["end_date_filter"] = ""
                map["date_filter"] = ""
                map["currency_filter"] = ""
                activity?.let { presenterEarning.getEarningList(it, map) }
            }else {
                Utility.showToast(activity, getString(R.string.no_network_message))
            }
        }
    }

    override fun onAdapterItemClick(view: View?, index: Int, modelBean : ModelEarningsHome.DataBean.EarningDataBean) {
        val intent = Intent(activity, DetailPayout::class.java)
        intent.putExtra("transaction_id", modelBean.transaction_id)
        startActivity(intent)
    }

    override fun onSuccess(body: ModelEarningsHome?) {
        val status = body!!.status
        if (status == 1) {
            if (body.data?.earning_data?.size!! > 0){
                modelList.addAll(body.data?.earning_data!!)
                adapter.notifyDataSetChanged()

                binding.noDataFound.visibility = View.GONE
                binding.recyclerPayout.visibility = View.VISIBLE

            }else{
                binding.noDataFound.visibility = View.VISIBLE
                binding.recyclerPayout.visibility = View.GONE
            }
        }else{
            binding.noDataFound.visibility = View.VISIBLE
            binding.recyclerPayout.visibility = View.GONE
        }
    }


}