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
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.teletap.R
import com.teletap.utilities.ConstantValues
import com.teletap.utilities.SharedPreferenceUtility

class PaypalBankAccountDialog : DialogFragment() {
    lateinit var mview: View
    var btnEdit: AppCompatButton? = null
    var btnCancel: AppCompatButton? = null
    var tvEmail:TextView? = null
    lateinit var imgClose : ImageView

    private var completionCallback: PaypalBankAccountDialogInterface? = null

    interface PaypalBankAccountDialogInterface {
        fun onEditClick()
        fun onCancelClick()
    }

    fun setDataCompletionCallback(completionCallback: PaypalBankAccountDialogInterface?) {
        this.completionCallback = completionCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mview = inflater.inflate(R.layout.dialog_verified_bank_account_layout, container, false)

        /*if (arguments != null) {
            textHeading = requireArguments().getString("text")
        }*/

        btnEdit = mview.findViewById(R.id.btnEdit)
        btnCancel = mview.findViewById(R.id.btnCancel)
        tvEmail = mview.findViewById(R.id.tvEmail)



        //imgClose.setOnClickListener { dismiss() }


        return mview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*textH?.text = textHeading*/

        tvEmail?.text = (SharedPreferenceUtility.getInstance().get(ConstantValues.PayPalAccountEmail))
        btnCancel?.setOnClickListener { completionCallback?.onCancelClick() }
        btnEdit?.setOnClickListener { completionCallback?.onEditClick() }

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

