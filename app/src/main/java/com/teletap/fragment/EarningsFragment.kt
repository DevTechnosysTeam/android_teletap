package com.teletap.fragment

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
import com.teletap.adapter.AdapterEarnings
import com.teletap.adapter.AdapterTransitionHistoryCD
import com.teletap.databinding.EarningsFragmentLayoutBinding
import com.teletap.model.ModelEarningsHome
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.EarningHomePresenter
import com.teletap.utilities.Utility
import com.teletap.view.IHomeEarningsView
import java.util.HashMap

class EarningsFragment : BaseFragment() , IHomeEarningsView, AdapterEarnings.OnItemClickListener{

    lateinit var binding : EarningsFragmentLayoutBinding
    lateinit var adapter : AdapterEarnings
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
    ): View {
        // Inflate the layout for this fragment

        binding  = DataBindingUtil.inflate(inflater, R.layout.earnings_fragment_layout, container, false)
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

        binding.recyclerHomeFrag.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { AdapterEarnings(it, modelList, this) }!!
        binding.recyclerHomeFrag.adapter = adapter
        adapter.notifyDataSetChanged()

        getEarningListApi()

        return binding.root
    }

    private fun getEarningListApi(){
        if (userInfo.token!=null){
            if (Utility.hasConnection(activity)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["date_filter"] = ""
                map["currency_filter"] = ""
                activity?.let { presenterEarning.getEarningList(it, map) }
            }else {
                Utility.showToast(activity, getString(R.string.no_network_message))
            }
        }
    }

    override fun onAdapterItemClick(view: View?, index: Int) {

    }

    override fun onSuccess(body: ModelEarningsHome?) {
        val status = body!!.status
        if (status == 1) {
            binding.tvTotalAmount.text = body.data?.total_earnings
            if (body.data?.earning_data?.size!! > 0){
                modelList.addAll(body.data?.earning_data!!)
                adapter.notifyDataSetChanged()

                binding.noDataFoundH.visibility = View.GONE
                binding.recyclerHomeFrag.visibility = View.VISIBLE

            }else{
                binding.noDataFoundH.visibility = View.VISIBLE
                binding.recyclerHomeFrag.visibility = View.GONE
            }
        }else{
            binding.noDataFoundH.visibility = View.VISIBLE
            binding.recyclerHomeFrag.visibility = View.GONE
        }
    }


}