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
import com.teletap.adapter.AdapterTransitionHistoryCD
import com.teletap.databinding.EarningsFragmentLayoutBinding
import com.teletap.dialogFragment.FilterDateHomeDialog
import com.teletap.dialogFragment.FilterHomeDialog
import com.teletap.model.ModelEarningsHome
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.EarningHomePresenter
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.SharedPreferenceUtility
import com.teletap.utilities.Utility
import com.teletap.view.IHomeEarningsView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class EarningsFragment : BaseFragment() , IHomeEarningsView, AdapterEarnings.OnItemClickListener{

    lateinit var binding : EarningsFragmentLayoutBinding
    lateinit var adapter : AdapterEarnings
    lateinit var presenterEarning : EarningHomePresenter

    lateinit var userInfo: SignupModel.DataBean
    private var token : String = ""
    private var modelList: ArrayList<ModelEarningsHome.DataBean.EarningDataBean> = ArrayList<ModelEarningsHome.DataBean.EarningDataBean>()

    private var currentDate:String = ""
    private var endDate:String = ""
    private var startDate:String = ""
    private var dateText:String = ""
    private var currencySelected : String = ""
    private var isFilterApplied : Boolean = false

    lateinit var filterHomeDialog : FilterDateHomeDialog

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

         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            currentDate = current.format(formatter)
        } else {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
             currentDate = simpleDateFormat.format(Date())
        }

        Log.e("currentDate", currentDate)

        binding.recyclerHomeFrag.layoutManager = LinearLayoutManager(activity)
        adapter = activity?.let { AdapterEarnings(it, modelList, this) }!!
        binding.recyclerHomeFrag.adapter = adapter
        adapter.notifyDataSetChanged()

        getEarningListApi(currentDate, "", "AED")

        binding.tvTotalAmount.setOnClickListener {
            showFilterDialog()
        }

        return binding.root
    }

    private fun getEarningListApi(startDate : String, endDate : String, str_currency : String){
        if (userInfo.token!=null){
            if (Utility.hasConnection(activity)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["start_date_filter"] = ""+startDate
                map["end_date_filter"] = ""+endDate
                //map["date_filter"] = ""
                map["currency_filter"] = ""+str_currency
                activity?.let { presenterEarning.getEarningList(it, map) }
            }else {
                Utility.showToast(activity, getString(R.string.no_network_message))
            }
        }
    }

        override fun onAdapterItemClick(view: View?, index: Int, modelBean: ModelEarningsHome.DataBean.EarningDataBean) {
            val intent = Intent(activity, DetailPayout::class.java)
            intent.putExtra("transaction_id", modelBean.transaction_id)
            startActivity(intent)
        }

    override fun onSuccess(body: ModelEarningsHome?) {
        val status = body!!.status
        modelList.clear()
        if (status == 1) {
            binding.tvTotalAmount.text = body.data?.total_earnings
            SharedPreferenceUtility.getInstance().save(ConstantValues.chargeCurrencyExchange, body.data?.currency_exchange_charge)
            SharedPreferenceUtility.getInstance().save(ConstantValues.percentCurrencyExchange, body.data?.currency_exchange_percent)

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

    private fun showFilterDialog(){
        val bundle = Bundle()
        bundle.putString("currencyStr", currencySelected)
        bundle.putString("startDate", startDate)
        bundle.putString("endDate", endDate)
        bundle.putString("dateText", dateText)
        bundle.putBoolean("isFilterApplied", isFilterApplied)
        filterHomeDialog = FilterDateHomeDialog()
        filterHomeDialog.arguments = bundle
        filterHomeDialog.isCancelable = false
        filterHomeDialog.setDataCompletionCallback(object : FilterDateHomeDialog.FilterHomeDialogInterface{
            override fun onApplyClick(str_currency: String, str_startDate : String, str_endDate : String, str_dateText: String, isFilter : Boolean) {
                isFilterApplied = isFilter
                startDate = str_startDate
                endDate = str_endDate
                currencySelected = str_currency
                dateText = str_dateText
                getEarningListApi(str_startDate, str_endDate, str_currency)
                filterHomeDialog.dismiss()
            }

            override fun onCloseClick() {
                filterHomeDialog.dismiss()
            }

            override fun onClearFilter(){
                isFilterApplied = false
                startDate = ""
                endDate = ""
                currencySelected = "AED"
                dateText = ""
                getEarningListApi(currentDate, "", "AED")
                filterHomeDialog.dismiss()
            }

        })
        filterHomeDialog.show(childFragmentManager, "filterHomeDialog")

    }



}