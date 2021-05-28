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

class DialogProceedPayment : DialogFragment() {
    lateinit var mview: View
    var ocrText: TextView? = null
    var nfcText: TextView? = null
    lateinit var imgClose : ImageView

    private var completionCallback: ProceedPaymentInterface? = null

    interface ProceedPaymentInterface {
        fun onNfc()
        fun onOcr()
    }

    fun setDataCompletionCallback(completionCallback: ProceedPaymentInterface?) {
        this.completionCallback = completionCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mview = inflater.inflate(R.layout.proceed_payment_dialog_layout, container, false)

        /*if (arguments != null) {
            textHeading = requireArguments().getString("text")
        }*/

        ocrText = mview.findViewById(R.id.ocrText)
        nfcText = mview.findViewById(R.id.nfcText)
        imgClose = mview.findViewById(R.id.imgClose)

        imgClose.setOnClickListener { dismiss() }


        return mview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*textH?.text = textHeading*/

        nfcText?.setOnClickListener { completionCallback?.onNfc() }
        ocrText?.setOnClickListener { completionCallback?.onOcr() }

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