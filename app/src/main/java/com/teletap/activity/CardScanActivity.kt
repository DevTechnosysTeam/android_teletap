package com.teletap.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import androidx.databinding.DataBindingUtil
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import com.braintreepayments.cardform.view.CardForm
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.CardScanActivityBinding
import com.teletap.dialogFragment.DialogPaymentSuccessful
import com.teletap.model.PaymentCardModel
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.PaymentCardPresenter
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.SharedPreferenceUtility
import com.teletap.utilities.Utility
import com.teletap.view.IPaymentCardView
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.HashMap

class CardScanActivity : BaseActivity(), IPaymentCardView {

    lateinit var binding : CardScanActivityBinding
    lateinit var cardForm : CardForm
    private var TAG : String = "cardScanResult"

    private var amount : String = "0.0"
    private var totalAmount : String = "0.0"
    private var tax : String = "0.0"
    private var description : String = ""
    private var currency_type : String = ""

    private val REQUEST_CODE_SCAN_CARD = 1

    lateinit var userInfo: SignupModel.DataBean

    lateinit var presenterCard: PaymentCardPresenter
    private var currencyExchangePercent:Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.card_scan_activity)
        cardForm = findViewById(R.id.card_form)
        if (intent!=null) {
            totalAmount = intent.getStringExtra("totalAmount").toString()
            amount = intent.getStringExtra("amount").toString()
            tax = intent.getStringExtra("tax").toString()
            description = intent.getStringExtra("description").toString()
        }
        try {
            userInfo = AppPreference.getUserInfo(this@CardScanActivity)
            Log.e("userInfo", userInfo.id.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        currencyExchangePercent = SharedPreferenceUtility.getInstance().get(ConstantValues.percentCurrencyExchange)

        if (SharedPreferenceUtility.getInstance().get(ConstantValues.isCurrencyUSD, false)){
            currency_type = "USD"
            currencyExchangePercent = SharedPreferenceUtility.getInstance().get(ConstantValues.percentCurrencyExchange)
        }else{
            currency_type = "AED"
            currencyExchangePercent = 0.0f
        }

        binding.toolBarCS.title.text = getString(R.string.payment)
        binding.toolBarCS.imgBack.setOnClickListener{onBackPressed()}

        presenterCard = PaymentCardPresenter()
        presenterCard.view = this

        cardForm.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_OPTIONAL)
            .postalCodeRequired(false)
            .mobileNumberRequired(false)
            .mobileNumberExplanation("")
            .setup(this@CardScanActivity)

        cardForm.cvvEditText.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        val intent = ScanCardIntent.Builder(this).build()
        startActivityForResult(intent, REQUEST_CODE_SCAN_CARD)

        binding.btnConfirm.setOnClickListener {

            if (TextUtils.isEmpty(cardForm.cardholderNameEditText.text.toString().trim())){
                Utility.showToast(this@CardScanActivity, "Please enter name")
            }else if (TextUtils.isEmpty(cardForm.cardEditText.text.toString().trim())){
                Utility.showToast(this@CardScanActivity, "Please enter card number")
            }else if (TextUtils.isEmpty(cardForm.expirationDateEditText.text.toString().trim())){
                Utility.showToast(this@CardScanActivity, "Please enter expiration date")
            }else if (TextUtils.isEmpty(cardForm.cvvEditText.text.toString().trim())){
                Utility.showToast(this@CardScanActivity, "Please enter cvv")
            }else{
                if (Utility.hasConnection(this@CardScanActivity)) {
                    val map: MutableMap<String, Any> = HashMap()
                    map["token"] = ""+userInfo.token!!
                    map["amount"] =""+ amount
                    map["card_number"] =""+ cardForm.cardEditText.text.toString().trim()
                    map["month"] =""+ cardForm.expirationMonth
                    map["year"] ="20"+ cardForm.expirationYear
                    map["cvv"] =""+ cardForm.cvv
                    map["description"] =""+ description
                    map["currency_type"] =""+ currency_type
                    map["total_amount"] =""+ totalAmount
                    map["tax"] =""+ tax
                    map["currency_exchange_percent"] =""+ currencyExchangePercent
                    presenterCard.doCardPaymentApi(this@CardScanActivity, map)
                }else {
                    Utility.showToast(this@CardScanActivity, getString(R.string.no_network_message))
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_CODE_SCAN_CARD) {
            if (resultCode === RESULT_OK) {
                val card: Card? = data?.getParcelableExtra(ScanCardIntent.RESULT_PAYCARDS_CARD)
                val cardData = """
            Card number: ${card?.cardNumberRedacted.toString()}
            Card holder: ${card?.cardHolderName.toString()}
            Card expiration date: ${card?.expirationDate}
            """.trimIndent()
                cardForm.cardEditText.setText(card?.cardNumberRedacted.toString())
                cardForm.expirationDateEditText.setText(card?.expirationDate.toString())
                Log.i(TAG, "Card info: $cardData")
            } else if (resultCode === RESULT_CANCELED) {
                Log.i(TAG, "Scan canceled")
            } else {
                Log.i(TAG, "Scan failed")
            }
        }
    }

    override fun onSuccess(body: PaymentCardModel?) {
        val status = body!!.status
        if (status == 1) {
            showPaymentSuccessDialog(body)

            //Utility.shortToast(this@CardScanActivity, body.message)

        }else{
            Utility.shortToast(this@CardScanActivity, body.message)
        }
    }

    override fun getContext(): Context {
        return this
    }

    private fun showPaymentSuccessDialog(body: PaymentCardModel?){
        val dialogPaymentSuccessful = DialogPaymentSuccessful()
        dialogPaymentSuccessful.setDataCompletionCallback(object : DialogPaymentSuccessful.PaymentInterface{
            override fun onClose(){
                dialogPaymentSuccessful.dismiss()
                val intent = Intent(this@CardScanActivity, HomeActivity::class.java)
                startActivity(intent)
            }
            override  fun onShareInvoice(){
                dialogPaymentSuccessful.dismiss()
                val intent = Intent(this@CardScanActivity, ContactsListActivity::class.java)
                intent.putExtra("cameFrom", "shareInvoice")
                intent.putExtra("transaction_id", body?.data?.transaction_id)
                startActivity(intent)
                finish()
            }
        }
            )
        dialogPaymentSuccessful.show(supportFragmentManager, "dialogPaymentSuccessful")
    }
}