package com.teletap.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.teletap.R
import com.teletap.activity.CardScanActivity
import com.teletap.databinding.PaymentFragmentLayoutBinding
import com.teletap.dialogFragment.DialogProceedPayment
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.SharedPreferenceUtility
import com.teletap.utilities.Utility
import java.math.RoundingMode
import java.text.DecimalFormat


open class PaymentFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding : PaymentFragmentLayoutBinding

    private val paymentBroadcastReceiver = PaymentBroadcastReceiver()

    private var currencyExchangeRate : Float = 0.0f
    private var percentCurrencyExchange : Float = 0.0f

    private var taxApplicable : Float = 0.0f

    private var totalAmount: Float = 0.0f
    private var enteredAmount: Float = 0.0f

    private var currencyString : String = ""

    //usd ---- 1usd == 3.67aed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater, R.layout.payment_fragment_layout, container, false)

        if (SharedPreferenceUtility.getInstance().get(ConstantValues.isCurrencyUSD, false)){
            binding.edAmount.hint = getString(R.string.enter_amount_usd)
            binding.tvExchangeCharge.visibility = View.VISIBLE
        }else{
            binding.edAmount.hint = getString(R.string.enter_amount_aed)
            binding.tvExchangeCharge.visibility = View.GONE
        }

        currencyExchangeRate = SharedPreferenceUtility.getInstance().get(ConstantValues.chargeCurrencyExchange)

        percentCurrencyExchange = SharedPreferenceUtility.getInstance().get(ConstantValues.percentCurrencyExchange)


        binding.imgNumericKeyboard.setOnClickListener(this)
        binding.imgKeyboard.setOnClickListener(this)
        binding.btnCharge.setOnClickListener(this)

        binding.edAmount.setOnFocusChangeListener { view, b ->
            if (b){
                activity?.let { hideKeyboard(it) }
                binding.keyboardCustom.visibility = View.VISIBLE
            }
        }

        binding.edTaxApplicable.setOnFocusChangeListener { view, b ->
            if (b){
                binding.keyboardCustom.visibility = View.GONE
            }
        }

        binding.edDescription.setOnFocusChangeListener { view, b ->
            if (b){
                binding.keyboardCustom.visibility = View.GONE
            }
        }

        return binding.root
    }

    private fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        val methodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assert(methodManager != null && view != null)
        methodManager.hideSoftInputFromWindow(
            view!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun showKeyboard(activity: Activity) {
        val view = activity.currentFocus
        val methodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assert(methodManager != null && view != null)
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when(p0.id){
                R.id.btnCharge -> {
                    Utility.hideKeyboard(activity)

                    if (TextUtils.isEmpty(binding.edAmount.text.toString().trim())){
                        Utility.shortToast(activity, "Please enter amount")
                    }else if (binding.edAmount.text.toString().trim().toDouble() == 0.0){
                        Utility.shortToast(activity, "Please enter valid amount")
                    }else {
                        if (SharedPreferenceUtility.getInstance().get(ConstantValues.isCurrencyUSD, false)){
                            usdCalculateTotalAmount()
                        }else {
                            aedCalculateTotalAmount()
                        }
                    }
                }
                R.id.imgNumericKeyboard -> {
                    binding.imgNumericKeyboard.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.active_numeric_keyborad
                        )
                    })
                    binding.imgKeyboard.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.msg_keyboard
                        )
                    })

                    binding.edAmount.requestFocus()
                    binding.keyboardCustom.visibility = View.VISIBLE
                    activity?.let { hideKeyboard(it) }
                }
                R.id.imgKeyboard -> {
                    binding.imgKeyboard.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.active_msg_keyboard
                        )
                    })
                    binding.imgNumericKeyboard.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.numeric_keyboard
                        )
                    })

                    binding.keyboardCustom.visibility = View.GONE
                    binding.edDescription.requestFocus()
                    activity?.let { showKeyboard(it) }
                }
            }
        }
    }

    private fun showProceedDialog(){
        val bundle = Bundle()
        bundle.putFloat("totalAmount", totalAmount)
        bundle.putString("currencySymbol", currencyString)
        val dialogProceedPayment = DialogProceedPayment()
        dialogProceedPayment.arguments= bundle
        dialogProceedPayment.isCancelable = false
        dialogProceedPayment.setDataCompletionCallback(object :
            DialogProceedPayment.ProceedPaymentInterface {
            override fun onNfc() {
                //showPaymentSuccessDialog()
                val intent = Intent(activity, com.teletap.activity.NfcActivity::class.java)
                intent.putExtra("totalAmount", totalAmount.toString())
                intent.putExtra("amount", enteredAmount.toString())
                intent.putExtra("tax", binding.edTaxApplicable.text.toString().trim())
                intent.putExtra("description", binding.edDescription.text.toString().trim())
                startActivity(intent)

                dialogProceedPayment.dismiss()
            }

            override fun onOcr() {
                val intent = Intent(activity, CardScanActivity::class.java)
                intent.putExtra("totalAmount", totalAmount.toString())
                intent.putExtra("amount", enteredAmount.toString())
                intent.putExtra("tax", binding.edTaxApplicable.text.toString().trim())
                intent.putExtra("description", binding.edDescription.text.toString().trim())
                startActivity(intent)

                dialogProceedPayment.dismiss()
            }
        })
        dialogProceedPayment.show(childFragmentManager, "dialogProceedPayment")
    }

    private fun aedCalculateTotalAmount() {
        if (!TextUtils.isEmpty(binding.edAmount.text.toString().trim())){
            enteredAmount = binding.edAmount.text.toString().trim().toFloat()
            if (!TextUtils.isEmpty(binding.edTaxApplicable.text.toString().trim())) {
                taxApplicable = binding.edTaxApplicable.text.toString().trim().toFloat()
            }
            totalAmount = enteredAmount + ((taxApplicable*100)/enteredAmount)
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.HALF_EVEN
            totalAmount = df.format(totalAmount).toFloat()
            binding.btnCharge.text = "Charge AED" + totalAmount

            currencyString = getString(R.string.aed)

            showProceedDialog()
        }
    }

    private fun usdCalculateTotalAmount() {
        if (!TextUtils.isEmpty(binding.edAmount.text.toString().trim())){
            val tempAmountEntered = binding.edAmount.text.toString().trim().toFloat()
            if (!TextUtils.isEmpty(binding.edTaxApplicable.text.toString().trim())) {
                taxApplicable = binding.edTaxApplicable.text.toString().trim().toFloat()
            }
            enteredAmount = (tempAmountEntered)/currencyExchangeRate.toFloat()
            val exchangeTax = (enteredAmount*percentCurrencyExchange.toFloat())/100
            totalAmount = enteredAmount + ((taxApplicable*100)/enteredAmount)+exchangeTax
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.HALF_EVEN
            totalAmount = df.format(totalAmount).toFloat()
            binding.btnCharge.text = "Charge $"+ totalAmount

            currencyString = "$"

            showProceedDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                paymentBroadcastReceiver,
                IntentFilter("CurrencyUpdate")
            )
        }
    }

    inner class PaymentBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent){
            when (intent.action) {
                "CurrencyUpdate" -> {
                    val data = intent.getBooleanExtra("isCurrencyUSD", false)
                    if (data) {
                        binding.edAmount.hint = getString(R.string.enter_amount_usd)
                        binding.tvExchangeCharge.visibility = View.VISIBLE
                        usdCalculateTotalAmount()
                    }else{
                        binding.edAmount.hint = getString(R.string.enter_amount_aed)
                        binding.tvExchangeCharge.visibility = View.GONE
                        aedCalculateTotalAmount()
                    }
                }
                //else -> Toast.makeText(context, "Action Not Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(paymentBroadcastReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(paymentBroadcastReceiver)
        }
    }

}