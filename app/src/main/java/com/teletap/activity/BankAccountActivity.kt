package com.teletap.activity

import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.BankAccountActivityBinding
import com.teletap.model.BankAccountModel
import com.teletap.model.SignupModel
import com.teletap.model.SimpleModel
import com.teletap.presenterClasses.BankAccountPresenter
import com.teletap.utilities.Utility
import com.teletap.view.IBankAccView
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*
import java.util.HashMap

class BankAccountActivity : BaseActivity(), IBankAccView {

    lateinit var binding: BankAccountActivityBinding
    private lateinit var webView : WebView
    private lateinit var progressBar : ProgressBar
    private var urlWeb : String = "https://teletap.devtechnosys.info/connect"

    lateinit var userInfo: SignupModel.DataBean
    lateinit var presenterBankAcc : BankAccountPresenter
    private var paypalEmail : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bank_account_activity)
        webView = findViewById(R.id.webViewBA)
        progressBar = findViewById(R.id.progressBarBA)
        presenterBankAcc = BankAccountPresenter()
        presenterBankAcc.view = this

        try {
            userInfo = AppPreference.getUserInfo(this@BankAccountActivity)
            Log.e("userInfo", userInfo.token.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        binding.toolbarBA.title.text = "Bank Account"
        binding.toolbarBA.imgBack.setOnClickListener { onBackPressed() }

        webView.settings.loadsImagesAutomatically = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        // Configure the client to use when opening URLs
        webView.webViewClient = xWebViewClient()
        // Load the initial URL
        webView.loadUrl(urlWeb)

    }

    override fun onResume() {
        super.onResume()
        webView.webViewClient = xWebViewClient()
    }

    inner class xWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            //progressBar.visibility = View.VISIBLE
            view.loadUrl(url)
            try {
                Log.e("returnUrl", url)
                if (url.contains("https://teletap.devtechnosys.info/payment/paymant_connect?email=")) {
                    paypalEmail = url.substringAfter("email=")
                    if (paypalEmail!=""){
                        if (Utility.hasConnection(this@BankAccountActivity)) {
                            val map: MutableMap<String, Any> = HashMap()
                            map["token"] = ""+ userInfo.token.toString()
                            map["paypal_email"] = ""+ paypalEmail
                            presenterBankAcc.updatePayPalTokenApi(this@BankAccountActivity, map)
                        }else {
                            Utility.showToast(this@BankAccountActivity, getString(R.string.no_network_message))
                        }
                    }
                }
            } catch (exception: ArrayIndexOutOfBoundsException) {
                exception.printStackTrace()
                //                view.loadUrl(url);
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            //progressBar.visibility = View.GONE
            /*try {
                if (url.equals("https://masqar.com/PaymentWebservices/authorizeApplicationOauth")){
                    */
            /*Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();*/
            /*
                    Intent intent = new Intent(StripeAccountActivity.this, HomeActivity.class);
                    intent.putExtra("cameFrom", "stripeAccount");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

            }
            catch (ArrayIndexOutOfBoundsException exception){
                exception.printStackTrace();
            }*/
        }

        fun onPageStarted(view: WebView?, url: String?) {
            /*if (url.equalsIgnoreCase("https://pay-dev.pulpal.az/payment-status")) {
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }*/
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            Log.e("error", "Error   Unexpected error occurred.Reload page again.")
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            Log.e("error", "HttpError Unexpected error occurred.Reload page again.")
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            super.onReceivedSslError(view, handler, error)
            Log.e("error", "Unexpected SSL error occurred.Reload page again.")
        }
    }

    override fun onSuccess(body: BankAccountModel?) {
        val status = body!!.status
        if (status == 1){
            Utility.showToast(this@BankAccountActivity, body.message)
            setResult(RESULT_OK)
            finish()
        }else{
            Utility.showToast(this@BankAccountActivity, body.message)
            finish()
        }
    }

    override fun getContext(): Context {
        return this
    }
}