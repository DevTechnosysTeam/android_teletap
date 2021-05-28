package com.teletap.activity

import android.graphics.Bitmap
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import com.teletap.R
import com.teletap.databinding.BusinessProfileActivityBinding
import com.teletap.databinding.PrivacyPolicyActivityBinding
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*

class PrivacyPolicy : AppCompatActivity() {
    lateinit var binding: PrivacyPolicyActivityBinding
    private var title : String = ""
    private var webUrl : String = ""
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.privacy_policy_activity)

        intent!=null
        title = intent.getStringExtra("title").toString()
        webUrl = intent.getStringExtra("webUrl").toString()

        binding.toolBar.imgBack.setOnClickListener { onBackPressed() }
        binding.toolBar.title.text = title

        progressBar = this.findViewById(R.id.progressBar)
        val webView = this.findViewById(R.id.webView) as WebView
        val webSettings: WebSettings = webView.settings
        val settings = webView.settings
        settings.domStorageEnabled = true
        webView.webViewClient = MyWebViewClient()
        webSettings.javaScriptEnabled = true

        if (webUrl != "")
            webView.loadUrl(webUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        val webView = this.findViewById(R.id.webView) as WebView
        webView.destroy()
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            progressBar.visibility = View.VISIBLE
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            //refresh.setVisibility(View.GONE)
            //if (!webViewProgressBar.isShown()) webViewProgressBar.setVisibility(View.VISIBLE)
        }


        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            /*refresh.setVisibility(View.VISIBLE)
            if (prog.isShown()) webViewProgressBar.setVisibility(View.GONE)*/
            progressBar.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            //refresh.setVisibility(View.VISIBLE)
            //if (webViewProgressBar.isShown()) webViewProgressBar.setVisibility(View.GONE)
            Log.e("error", "Error   Unexpected error occurred.Reload page again.")
            /*Toast.makeText(
                this@DemoActivity,
                "Unexpected error occurred.Reload page again.",
                Toast.LENGTH_SHORT
            ).show()*/
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            //refresh.setVisibility(View.VISIBLE)
            //if (webViewProgressBar.isShown()) webViewProgressBar.setVisibility(View.GONE)
            Log.e("error", "HttpError Unexpected error occurred.Reload page again.")
            /*Toast.makeText(
                this@DemoActivity,
                "Unexpected error occurred.Reload page again.",
                Toast.LENGTH_SHORT
            ).show()*/
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            super.onReceivedSslError(view, handler, error)
            //refresh.setVisibility(View.VISIBLE)
            //if (webViewProgressBar.isShown()) webViewProgressBar.setVisibility(View.GONE)
            Log.e("error", "Unexpected SSL error occurred.Reload page again.")
            /*Toast.makeText(
                this@DemoActivity,
                "Unexpected SSL error occurred.Reload page again.",
                Toast.LENGTH_SHORT
            ).show()*/
        }
    }
}