package com.teletap.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codekidlabs.storagechooser.StorageChooser
import com.teletap.*
import com.teletap.adapter.AdapterCategory
import com.teletap.adapter.AdapterCountry
import com.teletap.databinding.AddBusinessActivityBinding
import com.teletap.databinding.PopupDialogLayoutBinding
import com.teletap.databinding.PopupListSearchDialogLayoutBinding
import com.teletap.model.*
import com.teletap.presenterClasses.AddBusiness_Presenter
import com.teletap.utilities.RealPathUtil
import com.teletap.utilities.Utility
import com.teletap.view.IAddBusinessView
import kotlinx.android.synthetic.main.business_profile_activity.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddBusiness : BaseActivity(), IAddBusinessView, AdapterCategory.OnItemClickListener,
    View.OnClickListener, AdapterCountry.OnItemClickListener/*, EasyPermissions.PermissionCallbacks*/ {

    lateinit var binding : AddBusinessActivityBinding

    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSION_EXTERNAL_STORAGE_CODE = 501
    private val PERMISSIONS_Photo = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var imagePath = ""

    var path: String? = null
    private var file: File? = null
    private val IMAGE_DIRECTORY_NAME = "Teletap"

    lateinit var userInfo: SignupModel.DataBean
    private var token : String = ""

    lateinit var presenterAddBusiness: AddBusiness_Presenter
    private val categoryList: ArrayList<ModelCategory.DataBean> = ArrayList<ModelCategory.DataBean>()
    lateinit var categoryAdapter : AdapterCategory
    private var category_id : Int = 0
    lateinit var dialogBinding : PopupDialogLayoutBinding
    lateinit var dialogCategory:Dialog

    //countryList
    private val countryList:ArrayList<ModelCountryList.DataBean> = ArrayList<ModelCountryList.DataBean>()
    private var countryId : Int = 0
    lateinit var countryDialogBinding : PopupListSearchDialogLayoutBinding
    lateinit var countryDialog : Dialog
    lateinit var countryAdapter : AdapterCountry

    var captureLicenseFile: Uri? = null
    var captureIdProofFile: Uri? = null

    private var uploadFileType : Int = 0

    private var doubleBackToExitPressedOnce = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.add_business_activity)
        presenterAddBusiness = AddBusiness_Presenter()
        presenterAddBusiness.view = this


        try {
            userInfo = AppPreference.getUserInfo(this@AddBusiness)
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }
        token = userInfo.token.toString()

        getBusinessProfileApi()

        binding.edSocialMedia.movementMethod = LinkMovementMethod.getInstance()
        binding.edWebsite.movementMethod = LinkMovementMethod.getInstance()

        binding.addImageAB.setOnClickListener(this)
        binding.btnBrowseLicense.setOnClickListener(this)
        binding.btnBrowseId.setOnClickListener(this)
        binding.btnSaveAB.setOnClickListener(this)
        binding.edCountryAB.setOnClickListener(this)
        binding.tvCategory.setOnClickListener(this)
        binding.imgBackAB.setOnClickListener(this)

    }

    private fun getBusinessProfileApi(){
        if (Utility.hasConnection(this@AddBusiness)) {
            val map: MutableMap<String, Any> = HashMap()
            map["token"] = ""+ token
            presenterAddBusiness.getBusinessProfileApi(this@AddBusiness, map)
        }else {
            Utility.showToast(this@AddBusiness, getString(R.string.no_network_message))
        }
    }

    private fun getCategoryList(){
        if (Utility.hasConnection(this@AddBusiness)) {
            val map: MutableMap<String, Any> = HashMap()
            map["token"] = ""+ token
            presenterAddBusiness.getCategoryListApi(this@AddBusiness, map)
        }else {
            Utility.showToast(this@AddBusiness, getString(R.string.no_network_message))
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
                    Toast.makeText(
                        this,
                        "something went wrong! please try again",
                        Toast.LENGTH_SHORT
                    ).show()
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
                        .placeholder(R.drawable.default_user_profile)
                ).load(imagePath).into(binding.roundedImageView)
            }
        }

    }

    /*private fun openUploadImageDialog(){
        val uploadImageDialog = UploadImageDialog()
        uploadImageDialog.setDataCompletionCallback(object :
            UploadImageDialog.UploadImageDialogInterface {
            override fun onCamera() {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureCamera =
                    getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.putExtra(MediaStore.EXTRA_OUTPUT, captureCamera)
                startActivityForResult(intent, PERMISSION_CALLBACK_CONSTANT)
                uploadImageDialog.dismiss()
            }

            override fun onGallery() {
                val takePictureIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(takePictureIntent, REQUEST_GALLERY)
                uploadImageDialog.dismiss()
            }

        })
        uploadImageDialog.show(supportFragmentManager, "uploadImageDialog")
    }*/

    override fun onCategorySuccess(body: ModelCategory?) {
        val status = body!!.status
        categoryList.clear()
        if (status == 1){
            body.data?.let { categoryList.addAll(it) }
            dialogBinding= DataBindingUtil.inflate<PopupDialogLayoutBinding>(
                LayoutInflater.from(
                    this@AddBusiness
                ),
                R.layout.popup_dialog_layout, binding.root as ViewGroup, false
            )
            dialogCategory= Dialog(Objects.requireNonNull(this@AddBusiness))
            dialogCategory.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogCategory.setContentView(dialogBinding.root)
            Objects.requireNonNull(dialogCategory.window)!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialogCategory.show()

            categoryAdapter = AdapterCategory(this@AddBusiness, categoryList, this)
            dialogBinding.recyclerView.layoutManager = LinearLayoutManager(this@AddBusiness)
            dialogBinding.recyclerView.itemAnimator = DefaultItemAnimator()
            dialogBinding.recyclerView.adapter = categoryAdapter
            categoryAdapter.notifyDataSetChanged()
        }
        else if (status ==0){
            Utility.showToast(this@AddBusiness, body.message)
        }

    }

    override fun onGetProfileSuccess(body: ModelGetBusiness?) {
        val status = body!!.status
        if (status == 1){
            if (body.data?.first_name!=null) {
                binding.edFirstNameAB.setText(body.data?.first_name)
            }
            if (body.data?.last_name!=null)
                binding.edLastNameAB.setText(body.data?.last_name)
        }
    }

    override fun getContext(): Context {
        return this
    }

    override fun onSelectedCategory(view: View?, index: Int, s: ModelCategory.DataBean) {
        category_id = s.id
        binding.tvCategory.setText(s.name)
        dialogCategory.dismiss()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.addImageAB -> {
                requestToUploadProfilePhoto()
            }
            R.id.tvCategory -> {
                Utility.hideKeyboard(this@AddBusiness)
                getCategoryList()
            }
            R.id.btnBrowseLicense -> {
                Utility.hideKeyboard(this@AddBusiness)
                uploadFileType = 1
                fileUploadRequest(1)
            }
            R.id.btnBrowseId -> {
                Utility.hideKeyboard(this@AddBusiness)
                uploadFileType = 2
                fileUploadRequest(2)
            }
            R.id.btnSaveAB -> {
                checkValidations()
            }
            R.id.edCountryAB -> {
                Utility.hideKeyboard(this@AddBusiness)
                if (Utility.hasConnection(this@AddBusiness)) {
                    val map: MutableMap<String, Any> = HashMap()
                    //map["token"] = ""+ token
                    presenterAddBusiness.getCountryListApi(this@AddBusiness, map)
                } else {
                    Utility.showToast(this@AddBusiness, getString(R.string.no_network_message))
                }
            }
            R.id.imgBackAB -> {
                onBackPressed()
            }
        }
    }

    private fun checkValidations() {
        if(TextUtils.isEmpty(binding.edFirstNameAB.text.toString().trim())) {
            Toast.makeText(this@AddBusiness, "Please enter first name", Toast.LENGTH_SHORT).show()
            binding.edFirstNameAB.requestFocus()
        }else if (TextUtils.isEmpty(binding.edLastNameAB.text.toString().trim())) {
            Toast.makeText(this@AddBusiness, "Please enter last name", Toast.LENGTH_SHORT).show()
            binding.edLastNameAB.requestFocus()
        }else if (TextUtils.isEmpty(binding.edBusinessNameAB.text.toString().trim())) {
            Toast.makeText(this@AddBusiness, "Please enter Business name", Toast.LENGTH_SHORT).show()
            binding.edBusinessNameAB.requestFocus()
        }else if (TextUtils.isEmpty(binding.edTaxNumberAB.text.toString().trim())) {
            Toast.makeText(this@AddBusiness, "Please enter Business Tax Number", Toast.LENGTH_SHORT).show()
            binding.edTaxNumberAB.requestFocus()
        }else if (TextUtils.isEmpty(binding.edDisplayNameAB.text.toString().trim())) {
            Toast.makeText(
                this@AddBusiness,
                "Please enter your business display name",
                Toast.LENGTH_SHORT
            ).show()
            binding.edDisplayNameAB.requestFocus()
        }else if (captureLicenseFile == null){
            Toast.makeText(
                this@AddBusiness,
                "Please select your shop/business license",
                Toast.LENGTH_SHORT
            ).show()
        }else if (captureIdProofFile == null){
            Toast.makeText(this@AddBusiness, "Please select your id proof", Toast.LENGTH_SHORT).show()
        }else if (category_id == 0){
            Toast.makeText(this@AddBusiness, "Please select category", Toast.LENGTH_SHORT).show()
        }else if (countryId == 0){
            Toast.makeText(this@AddBusiness, "Please select country", Toast.LENGTH_SHORT).show()
        }else{
            if (Utility.hasConnection(this@AddBusiness)) {
                updateProfileApi()
            }else{
                Utility.showToast(this@AddBusiness, getString(R.string.no_network_message))
            }
        }
    }

    private fun updateProfileApi() {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("token", token)
        builder.addFormDataPart("first_name", binding.edFirstNameAB.text.toString().trim())
        builder.addFormDataPart("last_name", binding.edLastNameAB.text.toString().trim())
        builder.addFormDataPart("business_name", binding.edBusinessNameAB.text.toString().trim())
        builder.addFormDataPart("display_name", binding.edDisplayNameAB.text.toString().trim())
        builder.addFormDataPart("business_tax_id", binding.edTaxNumberAB.text.toString().trim())
        builder.addFormDataPart("about_business", binding.edAboutAB.text.toString().trim())
        builder.addFormDataPart("website", binding.edWebsite.text.toString().trim())
        builder.addFormDataPart("social_media", binding.edSocialMedia.text.toString().trim())
        builder.addFormDataPart("category_id", "" + category_id)
        builder.addFormDataPart("country", "" + countryId)
        builder.addFormDataPart("state", "")
        builder.addFormDataPart("city", "")
        builder.addFormDataPart("zipcode", "")
        val file1: File?
        if (captureLicenseFile != null) {
            try {
                /*val path1: String? = RealPathUtil.getRealPath(
                    this@AddBusiness,
                    captureLicenseFile!!
                )
                file1 = File(path1)
                builder.addFormDataPart(
                    "license", file1.name, RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(), file1
                    )
                )*/
                file1 = File(captureLicenseFile.toString())
                builder.addFormDataPart(
                    "license", file1.name, RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(), file1
                    )
                )

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val file2: File?
        if (captureIdProofFile != null) {
            try {
                file2 = File(captureIdProofFile.toString())
                builder.addFormDataPart(
                    "id_proof", file2.name, RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(), file2
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //profile_image
        /*val file3: File?
        if (captureProfile != null) {
            try {
                file3 = FileUtil.from(this, captureProfile)
                builder.addFormDataPart("profile_image", Utility.getFileName(this, Uri.fromFile(file3)), RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), file3))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            builder!!.addFormDataPart("profile_picture", file.name, requestBody)
        }

        }*/
        if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            builder.addFormDataPart("business_image", file.name, requestBody)
        }
        val requestBody = builder.build()
        presenterAddBusiness.updateProfile(this@AddBusiness, requestBody)
    }

    override fun onUpdateBusinessProfileSuccess(body: ModelGetBusiness?) {
        val status = body!!.status
        if (status == 1){
            Toast.makeText(this@AddBusiness, body.message, Toast.LENGTH_SHORT).show()
            Session(this@AddBusiness).createLoginSession()
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onCountryListSuccess(body: ModelCountryList?) {
        val status = body!!.status
        countryList.clear()
        if (status == 1){
            body.data?.let { countryList.addAll(it) }
            countryDialogBinding = DataBindingUtil.inflate<PopupListSearchDialogLayoutBinding>(
                LayoutInflater.from(
                    this@AddBusiness
                ),
                R.layout.popup_list_search_dialog_layout, binding.root as ViewGroup, false
            )
            countryDialog = Dialog(Objects.requireNonNull(this@AddBusiness))
            countryDialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            countryDialog.setContentView(countryDialogBinding.root)
            Objects.requireNonNull(countryDialog.window)!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            countryDialog.show()
            countryDialogBinding.title.text = getString(R.string.select_country)
            countryDialogBinding.ivClose.setOnClickListener { countryDialog.dismiss() }

            countryAdapter = AdapterCountry(this@AddBusiness, countryList, this)
            countryDialogBinding.recyclerView.layoutManager = LinearLayoutManager(this@AddBusiness)
            countryDialogBinding.recyclerView.itemAnimator = DefaultItemAnimator()
            countryDialogBinding.recyclerView.adapter = countryAdapter
            countryAdapter.notifyDataSetChanged()

            countryDialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    filterCountryList(s.trim().toString(), countryList.toMutableList())
                }
            })

        }
        else if (status ==0){
            Utility.showToast(this@AddBusiness, body.message)
        }

    }

    private fun filterCountryList(
        toString: String,
        countryModel: MutableList<ModelCountryList.DataBean>
    ) {
        var countryModel_12: MutableList<ModelCountryList.DataBean> = countryModel
        countryModel_12 = java.util.ArrayList<ModelCountryList.DataBean>()
        for (item in countryList) {
            if (item.name?.toLowerCase(Locale.ROOT)!!.contains(
                    toString.toLowerCase(Locale.ROOT).toUpperCase(
                        Locale.ROOT
                    )
                )
                || item.name!!.toUpperCase(Locale.ROOT).contains(toString.toUpperCase(Locale.ROOT))) {
                countryModel_12.add(item)
            }
        }
        countryAdapter.filterCountryList(countryModel_12)
    }

    override fun onStateListSuccess(body: ModelState?) {

    }

    override fun onCityListSuccess(body: ModelCity?) {

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
        if (!hasPermissions(this@AddBusiness, *PERMISSIONS_Photo)) {
            ActivityCompat.requestPermissions(
                this@AddBusiness,
                PERMISSIONS_Photo,
                PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE
            )
        } else if (hasPermissions(this@AddBusiness, *PERMISSIONS_Photo)) {
            openCameraDialog()
        }
    }

    private fun fileUploadRequest(type: Int){
        if (!hasPermissions(this@AddBusiness, *PERMISSIONS_Photo)) {
            ActivityCompat.requestPermissions(
                this@AddBusiness,
                PERMISSIONS_Photo,
                PERMISSION_EXTERNAL_STORAGE_CODE
            )
        } else if (hasPermissions(this@AddBusiness, *PERMISSIONS_Photo)) {
            if (type == 1)
                licenseFileUpload()
            else
                idProofFileUpload()
        }
    }

    private fun licenseFileUpload(){
        val chooser = StorageChooser.Builder()
            .withActivity(this@AddBusiness)
            .withFragmentManager(fragmentManager)
            .withMemoryBar(true)
            .allowCustomPath(true)
            .setType(StorageChooser.FILE_PICKER)
            .build()
        chooser.setOnSelectListener { path ->
            Log.e("LicenseFile", path)

            if (path.contains("pdf") || path.contains("doc") || path.contains("PDF") || path.contains(
                    "DOC"
                ) || path.contains("xls") || path.contains("txt") || path.contains("png")
                || path.contains("jpeg") || path.contains("jpg")
            ) {
                captureLicenseFile = Uri.parse(path)
                Log.e("LicenseFile", path)
                binding.tvLicenseName.text = "File Selected"

            } else {
                Toast.makeText(
                    this@AddBusiness,
                    "Please select valid file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        chooser.show()
    }

    private fun idProofFileUpload(){
        val chooser2 = StorageChooser.Builder()
            .withActivity(this@AddBusiness)
            .withFragmentManager(fragmentManager)
            .withMemoryBar(true)
            .allowCustomPath(true)
            .setType(StorageChooser.FILE_PICKER)
            .build()
        chooser2.setOnSelectListener { path ->
            Log.e("LicenseFile", path)

            if (path.contains("pdf") || path.contains("doc") || path.contains("PDF") || path.contains(
                    "DOC"
                ) || path.contains("xls") || path.contains("txt") || path.contains("png")
                || path.contains("jpeg") || path.contains("jpg")
            ) {
                captureIdProofFile = Uri.parse(path)
                Log.e("IdFile", path)
                //val fileName = Utility.getFileName(this@AddBusiness, captureIdProofFile)
                //val file2 = FileUtil.from(this, captureIdProofFile)
                binding.tvIdProofName.text = "File Selected"

            } else {
                Toast.makeText(
                    this@AddBusiness,
                    "Please select valid file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        chooser2.show()
    }

    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(
            getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(
                R.string.cancel
            )
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items) { dialogInterface, i ->
            if (items[i] == getString(R.string.take_photo)) {
                captureImage()
            } else if (items[i] == getString(R.string.choose_from_gallery)) {
                chooseImage()
            } else if (items[i] == getString(R.string.cancel)) {
                dialogInterface.dismiss()
            }
        }
        builder.show()
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


    private fun getOutputMediaFileUri(type: Int): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(
                    type
                )!!
            )
        } else {
            Uri.fromFile(getOutputMediaFile(type))
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_DIRECTORY_NAME
        )
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File
        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
            File(
                mediaStorageDir.path + File.separator
                        + "IMG_" + timeStamp + ".png"
            )
        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            File(
                mediaStorageDir.path + File.separator
                        + "VID_" + timeStamp + ".mp4"
            )
        } else {
            return null
        }
        return mediaFile
    }


    private fun getRealPath(ur: Uri?): String? {
        var realpath = ""
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        // Get the cursor
        val cursor = contentResolver.query(
            ur!!,
            filePathColumn, null, null, null
        )!!
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty()) { /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                if (hasAllPermissionsGranted(grantResults)) {
                    openCameraDialog()
                } else {
                    Toast.makeText(
                        this,
                        "Please grant both Camera and Storage permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(
                    this,
                    "Please grant both Camera and Storage permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else if(requestCode == PERMISSION_EXTERNAL_STORAGE_CODE){
            if (grantResults.isNotEmpty()) { /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                if (hasAllPermissionsGranted(grantResults)) {
                    if (uploadFileType == 1)
                        licenseFileUpload()
                    else
                        idProofFileUpload()
                } else {
                    Toast.makeText(
                        this,
                        "Please grant both Camera and Storage permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(
                    this,
                    "Please grant both Camera and Storage permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSelectedCountry(view: View?, index: Int, s: ModelCountryList.DataBean) {
        countryId = s.id
        binding.edCountryAB.setText(s.name)
        countryDialog.dismiss()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.click_to_exit, Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}




