package com.teletap.fragment

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.appfoundry.nfclibrary.activities.NfcActivity
import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl
import com.teletap.R
import com.teletap.databinding.PaymentFragmentLayoutBinding
import com.teletap.dialogFragment.DialogPaymentSuccessful
import com.teletap.dialogFragment.DialogProceedPayment
import com.teletap.utilities.Utility


open class PaymentFragment : Fragment(), View.OnClickListener {
    lateinit var binding : PaymentFragmentLayoutBinding
    /*private var pendingIntent: PendingIntent? = null
    private lateinit var mIntentFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>
    private var mNfcAdapter: NfcAdapter? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(
            inflater,
            R.layout.payment_fragment_layout,
            container,
            false
        )

        /*mNfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        pendingIntent = PendingIntent.getActivity(
            activity, 0, Intent(activity, javaClass).addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            ), 0
        )
        mIntentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        mTechLists = arrayOf(
            arrayOf(Ndef::class.java.name),
            arrayOf(NdefFormatable::class.java.name)
        )*/

        binding.imgNumericKeyboard.setOnClickListener(this)
        binding.imgKeyboard.setOnClickListener(this)
        binding.btnCharge.setOnClickListener(this)

        return binding.root
    }


    private fun showPaymentSuccessDialog(){
        val dialogPaymentSuccessful = DialogPaymentSuccessful()
        dialogPaymentSuccessful.setDataCompletionCallback(object :
            DialogPaymentSuccessful.ProceedPaymentInterface {

        })
        dialogPaymentSuccessful.show(childFragmentManager, "dialogPaymentSuccessful")
    }

    private fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        val methodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assert(view != null)
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
                    if (TextUtils.isEmpty(binding.edAmount.text.toString().trim())){
                        Utility.shortToast(activity, "Please enter amount")
                    }else if (binding.edAmount.text.toString().trim().toDouble() == 0.0){
                        Utility.shortToast(activity, "Please enter valid amount")
                    }else {
                        val dialogProceedPayment = DialogProceedPayment()
                        dialogProceedPayment.isCancelable = false
                        dialogProceedPayment.setDataCompletionCallback(object :
                            DialogProceedPayment.ProceedPaymentInterface {
                            override fun onNfc() {
                                //showPaymentSuccessDialog()
                                val intent = Intent(activity, com.teletap.activity.NfcActivity::class.java)
                                intent.putExtra("amount", binding.edAmount.text.toString().trim())
                                startActivity(intent)

                                dialogProceedPayment.dismiss()
                            }

                            override fun onOcr() {
                                showPaymentSuccessDialog()
                            }
                        })
                        dialogProceedPayment.show(childFragmentManager, "dialogProceedPayment")
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

    /*private fun nfcFunctionality(){
        if (mNfcAdapter == null) {
            // This device doesn't support NFC.
            Toast.makeText(activity, "This device doesn't support NFC", Toast.LENGTH_SHORT).show()
        } else if (mNfcAdapter != null){

            if (!mNfcAdapter!!.isEnabled) {
                // NFC is Disabled
                Toast.makeText(activity, "NFC is Disabled", Toast.LENGTH_SHORT).show()
            } else {
                // NFC is enabled
                Toast.makeText(activity, "NFC is enabled", Toast.LENGTH_SHORT).show()

            }
        }
    }*/

    /*override fun onResume() {
        super.onResume()
        if (mNfcAdapter != null) {
            mNfcAdapter!!.enableForegroundDispatch(
                activity,
                pendingIntent,
                mIntentFilters,
                mTechLists
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mNfcAdapter!!.disableForegroundDispatch(activity)
        }
    }

    override fun onNewIntent(intent: Intent?) {
       super.onNewIntent(intent)
        val res = NfcReadUtilityImpl().readFromTagWithSparseArray(intent)
        for (i in 0 until res.size()) {
            Toast.makeText(activity, res.valueAt(i), Toast.LENGTH_SHORT).show()
        }
    }*/


}