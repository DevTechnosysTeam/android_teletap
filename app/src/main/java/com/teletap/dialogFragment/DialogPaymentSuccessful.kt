package com.teletap.dialogFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.teletap.R

class DialogPaymentSuccessful : DialogFragment() {
    lateinit var mview: View
    //var ocrText: TextView? = null
    var shareInvoice : TextView? = null
    lateinit var imgClose : ImageView

    private var completionCallback: PaymentInterface? = null

    interface PaymentInterface {
        fun onShareInvoice()
        fun onClose()
    }

    fun setDataCompletionCallback(completionCallback: PaymentInterface?) {
        this.completionCallback = completionCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mview = inflater.inflate(R.layout.dialog_payment_successful_layout, container, false)

        /*if (arguments != null) {
            textHeading = requireArguments().getString("text")
        }*/
        //ocrText = mview.findViewById(R.id.ocrText)
        shareInvoice = mview.findViewById(R.id.shareInvoice)
        imgClose = mview.findViewById(R.id.imgClose)

        return mview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*textH?.text = textHeading*/

        imgClose.setOnClickListener { completionCallback?.onClose() }

        shareInvoice?.setOnClickListener { completionCallback?.onShareInvoice() }
        //ocrText?.setOnClickListener { completionCallback?.onOcr() }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation4 //style id
        return dialog
    }

}