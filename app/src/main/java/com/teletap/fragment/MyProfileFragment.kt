package com.teletap.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.BuildConfig
import com.teletap.R
import com.teletap.databinding.MyProfileFragmentLayoutBinding
import com.teletap.dialogFragment.UploadImageDialog
import com.teletap.model.ProfileModel
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.HomePresenter
import com.teletap.presenterClasses.ProfilePresenter
import com.teletap.utilities.FileUtil
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.IProfileView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyProfileFragment : BaseFragment(), IProfileView, View.OnClickListener {

    lateinit var binding : MyProfileFragmentLayoutBinding
    lateinit var presenterProfile : ProfilePresenter
    lateinit var userInfo: SignupModel.DataBean
    private var isChecked : Boolean = false
    private var token : String = ""
    var captureProfile: Uri? = null

    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS_Photo = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var imagePath = ""
    private val IMAGE_DIRECTORY_NAME = "Teletap"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater, R.layout.my_profile_fragment_layout, container, false)
        presenterProfile = ProfilePresenter()
        presenterProfile.view = this
        binding.addImage.setOnClickListener(this)
        binding.btnSaveP.setOnClickListener(this)

        try {
            userInfo = AppPreference.getUserInfo(context)
            Log.e("userInfo", userInfo.id.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }
        token = userInfo.token.toString()

        getUserProfileApi()

        binding.checkboxP.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                binding.changePassLayout.visibility = View.VISIBLE
                isChecked = true
            }else{
                binding.changePassLayout.visibility = View.GONE
                isChecked = false
            }
        }


        return binding.root
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.addImage -> {
                /*val uploadImageDialog = UploadImageDialog()
                uploadImageDialog.setDataCompletionCallback(object : UploadImageDialog.UploadImageDialogInterface{
                    override fun onCamera() {

                    }

                    override fun onGallery() {

                    }

                })
                uploadImageDialog.show(childFragmentManager, "uploadImageDialog")*/
                requestToUploadProfilePhoto()
            }
            R.id.btnSaveP -> {
                checkValidations()
            }

        }
    }

    private fun checkValidations() {
        if (TextUtils.isEmpty(binding.edFirstNameP.text.toString().trim())) {
            Toast.makeText(activity, "Please enter first name", Toast.LENGTH_SHORT).show()
            binding.edFirstNameP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edLastNameP.text.toString().trim())) {
            Toast.makeText(activity, "Please enter last name", Toast.LENGTH_SHORT).show()
            binding.edLastNameP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edEmailP.text.toString().trim())) {
            Toast.makeText(activity, "Please enter email", Toast.LENGTH_SHORT).show()
            binding.edEmailP.requestFocus()
        }else if (!Utility.isValidEmail(binding.edEmailP.text.toString().trim())) {
            Toast.makeText(activity, "Please enter valid email", Toast.LENGTH_SHORT).show()
            binding.edEmailP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edMobileNoP.text.toString().trim())) {
            Toast.makeText(activity, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            binding.edMobileNoP.requestFocus()
        }else if (binding.edMobileNoP.text?.length!! < 7) {
            Toast.makeText(activity, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            binding.edMobileNoP.requestFocus()
        }else if (isChecked){
            if (TextUtils.isEmpty(binding.edCurrentPassP.text.toString().trim())) {
                Toast.makeText(activity, "Please enter current password", Toast.LENGTH_SHORT).show()
                binding.edCurrentPassP.requestFocus()
            }else if (TextUtils.isEmpty(binding.edNewPassP.text.toString().trim())) {
                Toast.makeText(activity, "Please enter new password", Toast.LENGTH_SHORT).show()
                binding.edNewPassP.requestFocus()
            }else if (binding.edNewPassP.text?.length!! < 7) {
                Toast.makeText(activity, "Password must contain at least 7 characters", Toast.LENGTH_SHORT).show()
                binding.edNewPassP.requestFocus()
            }else if (TextUtils.isEmpty(binding.edConfirmPassP.text.toString().trim())) {
                Toast.makeText(activity, "Please enter confirm password", Toast.LENGTH_SHORT).show()
                binding.edConfirmPassP.requestFocus()
            }else if(!Utility.isPasswordSame(binding.edNewPassP.text.toString(), binding.edConfirmPassP.text.toString())){
                Toast.makeText(activity, "Confirm Password does not match", Toast.LENGTH_SHORT).show()
            }else{
                if (Utility.hasConnection(activity)) {
                    updateUserProfileApi()
                }else {
                    Utility.showToast(activity, getString(R.string.no_network_message))
                }
            }

        }else{
            if (Utility.hasConnection(activity)) {
                updateUserProfileApi()
            }else {
                Utility.showToast(activity, getString(R.string.no_network_message))
            }

        }
    }

    private fun updateUserProfileApi() {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("token", token)
        builder.addFormDataPart("first_name", binding.edFirstNameP.text.toString().trim())
        builder.addFormDataPart("last_name", binding.edLastNameP.text.toString().trim())
        builder.addFormDataPart("mobile", binding.edMobileNoP.text.toString().trim())
        builder.addFormDataPart("email", binding.edEmailP.text.toString().trim())
        if (isChecked)
            builder.addFormDataPart("password", binding.edNewPassP.text.toString().trim())
        /*val file3: File?
        if (captureProfile != null) {
            try {
                file3 = FileUtil.from(activity, captureProfile)
                builder.addFormDataPart("profile_image", Utility.getFileName(activity, Uri.fromFile(file3)), RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), file3))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }*/
        if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            builder.addFormDataPart("profile_image", file.name, requestBody)
        }
        val requestBody = builder.build()
        activity?.let { presenterProfile.updateProfile(it, requestBody) }
    }

    override fun onUpdateProfileSuccess(body: ProfileModel?) {
        val status = body!!.status
            if (status == 1) {
                Utility.showToast(activity, body.message)
                binding.changePassLayout.visibility = View.GONE
                binding.checkboxP.isChecked = false
                isChecked = false
            }else{
                Utility.showToast(activity, body.message)
            }
        }

    private fun getUserProfileApi(){
        if (userInfo.token!=null){
            if (Utility.hasConnection(activity)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                activity?.let { presenterProfile.getUserProfile(it, map) }
            }else {
                Utility.showToast(activity, getString(R.string.no_network_message))
            }
        }
    }

    override fun onProfileSuccess(body: ProfileModel?) {
        val status = body!!.status
        if (status == 1) {
            binding.edFirstNameP.setText(body.data?.first_name)
            binding.edLastNameP.setText(body.data?.last_name)
            binding.edEmailP.setText(body.data?.email)
            binding.edMobileNoP.setText(body.data?.mobile)

            activity?.let { Glide.with(it).asBitmap().load(body.data?.profile_image).into(binding.roundedImageView) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) { //previewCapturedImage();
                if (uri != null) {
                    imagePath = ""
                    Log.e("uri", uri.toString())
                    imagePath = uri!!.path!!
                    Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.default_user_profile))
                        .load("file:///$imagePath").into(binding.roundedImageView)
                    //changeProfilePic=true
                } else {
                    Toast.makeText(activity, "something went wrong! please try again", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                imagePath = ""
                val uri = data.data
                imagePath = if (uri.toString().startsWith("content")) {
                    getRealPath(uri)!!
                } else {
                    uri!!.path!!
                }
                Glide.with(this).applyDefaultRequestOptions(
                    RequestOptions()
                    .placeholder(R.drawable.default_user_profile)).load(imagePath).into(binding.roundedImageView)
            }
        }

    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun requestToUploadProfilePhoto() {
        if (!hasPermissions(activity, *PERMISSIONS_Photo)) {
            activity?.let { ActivityCompat.requestPermissions(it, PERMISSIONS_Photo, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE) }
        } else if (hasPermissions(activity, *PERMISSIONS_Photo)) {
            openCameraDialog()
        }
    }

    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel))
        val builder = activity?.let { AlertDialog.Builder(it) }
        builder?.setTitle(getString(R.string.add_photo))
        builder?.setItems(items) { dialogInterface, i ->
            if (items[i] == getString(R.string.take_photo)) {
                captureImage()
            } else if (items[i] == getString(R.string.choose_from_gallery)) {
                chooseImage()
            } else if (items[i] == getString(R.string.cancel)) {
                dialogInterface.dismiss()
            }
        }
        builder?.show()
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
    }


    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }


    private fun getOutputMediaFileUri(type: Int): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.let { FileProvider.getUriForFile(it, BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!) }
        } else {
            Uri.fromFile(getOutputMediaFile(type))
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(Date())
        val mediaFile: File
        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
            File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".png")
        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            File(mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }
        return mediaFile
    }


    private fun getRealPath(ur: Uri?): String? {
        var realpath = ""
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        // Get the cursor
        val cursor = activity?.contentResolver?.query(ur!!,
            filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        //Log.e("columnIndex", String.valueOf(MediaStore.Images.Media.DATA));
        realpath = cursor.getString(columnIndex)
        cursor.close()
        return realpath
    }

    private fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty()) { /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                if (hasAllPermissionsGranted(grantResults)) {
                    openCameraDialog()
                } else {
                    Toast.makeText(activity, "Please grant both Camera and Storage permissions", Toast.LENGTH_SHORT).show()
                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(activity, "Please grant both Camera and Storage permissions", Toast.LENGTH_SHORT).show()
            }
        }

    }
}