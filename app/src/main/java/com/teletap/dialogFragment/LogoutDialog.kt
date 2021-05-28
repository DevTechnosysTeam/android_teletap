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

class LogoutDialog : DialogFragment() {
    lateinit var mview: View
    var bt_yes: AppCompatButton? = null
    var bt_no: AppCompatButton? = null
    lateinit var imgClose : ImageView

    private var completionCallback: LogoutDialogInterface? = null

    interface LogoutDialogInterface {
        fun onYesClick()
        fun onNoClick()
    }

    fun setDataCompletionCallback(completionCallback: LogoutDialogInterface?) {
        this.completionCallback = completionCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mview = inflater.inflate(R.layout.dialog_logout_layout, container, false)

        /*if (arguments != null) {
            textHeading = requireArguments().getString("text")
        }*/

        bt_yes = mview.findViewById(R.id.bt_yes)
        bt_no = mview.findViewById(R.id.bt_no)
        //imgClose = mview.findViewById(R.id.imgClose)

        //imgClose.setOnClickListener { dismiss() }


        return mview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*textH?.text = textHeading*/

        bt_no?.setOnClickListener { completionCallback?.onNoClick() }
        bt_yes?.setOnClickListener { completionCallback?.onYesClick() }

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