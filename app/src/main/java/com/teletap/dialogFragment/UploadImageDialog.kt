package com.teletap.dialogFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teletap.R

class UploadImageDialog : BottomSheetDialogFragment() {

    lateinit var mview : View
    var llCamera : LinearLayout? = null
    var llGallery : LinearLayout? = null

    private var completionCallback: UploadImageDialogInterface? = null

    interface UploadImageDialogInterface {
        fun onCamera()
        fun onGallery()
    }

    fun setDataCompletionCallback(completionCallback: UploadImageDialogInterface?) {
        this.completionCallback = completionCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mview = inflater.inflate(R.layout.bottom_upload_image_dialog, container, false)

        llCamera = mview.findViewById(R.id.llCamera)
        llGallery = mview.findViewById(R.id.llGallery)

        return mview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        llCamera?.setOnClickListener { completionCallback?.onCamera() }
        llGallery?.setOnClickListener { completionCallback?.onGallery() }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        //dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation4 //style id
        return dialog
    }

}