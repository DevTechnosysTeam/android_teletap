package com.teletap.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.teletap.*
import com.teletap.adapter.AdapterCategory
import com.teletap.adapter.AdapterCity
import com.teletap.adapter.AdapterCountry
import com.teletap.adapter.AdapterState
import com.teletap.databinding.BusinessProfileActivityBinding
import com.teletap.databinding.PopupDialogLayoutBinding
import com.teletap.databinding.PopupListSearchDialogLayoutBinding
import com.teletap.model.*
import com.teletap.presenterClasses.AddBusiness_Presenter
import com.teletap.utilities.FileUtil
import com.teletap.utilities.Utility
import com.teletap.view.IAddBusinessView
import kotlinx.android.synthetic.main.business_profile_activity.*
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BusinessProfile : BaseActivity(), IAddBusinessView, AdapterCategory.OnItemClickListener, View.OnClickListener, AdapterCountry.OnItemClickListener,
        AdapterState.OnItemClickListener, AdapterCity.OnItemClickListener{
    lateinit var binding : BusinessProfileActivityBinding

    lateinit var userInfo: SignupModel.DataBean
    private var token : String = ""

    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS_Photo = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var imagePath = ""

    var path: String? = null
    private var file: File? = null
    private val IMAGE_DIRECTORY_NAME = "Teletap"

    lateinit var presenterAddBusiness: AddBusiness_Presenter
    private val categoryList: ArrayList<ModelCategory.DataBean> = ArrayList<ModelCategory.DataBean>()
    lateinit var categoryAdapter : AdapterCategory
    private var category_id : Int = 0
    lateinit var dialogBinding : PopupDialogLayoutBinding
    lateinit var dialogCategory: Dialog
    //countryList
    private val countryList:ArrayList<ModelCountryList.DataBean> = ArrayList<ModelCountryList.DataBean>()
    private var countryId : Int = 0
    lateinit var countryDialogBinding : PopupListSearchDialogLayoutBinding
    lateinit var countryDialog : Dialog
    lateinit var countryAdapter : AdapterCountry
    //stateList
    private val stateList:ArrayList<ModelState.DataBean> = ArrayList<ModelState.DataBean>()
    private var stateId : Int = 0
    lateinit var stateDialogBinding : PopupListSearchDialogLayoutBinding
    lateinit var stateDialog : Dialog
    lateinit var stateAdapter : AdapterState
    //cityList
    private val cityList:ArrayList<ModelCity.DataBean> = ArrayList<ModelCity.DataBean>()
    private var cityId : Int = 0
    lateinit var cityDialogBinding : PopupListSearchDialogLayoutBinding
    lateinit var cityDialog : Dialog
    lateinit var cityAdapter : AdapterCity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.business_profile_activity)

        binding.toolBarBP.imgBack.setOnClickListener { onBackPressed() }
        binding.toolBarBP.title.text = getString(R.string.business_profile)

        presenterAddBusiness = AddBusiness_Presenter()
        presenterAddBusiness.view = this
        try {
            userInfo = AppPreference.getUserInfo(this@BusinessProfile)
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }
        token = userInfo.token.toString()

        getBusinessProfileApi()

        binding.edSocialMedia.movementMethod = LinkMovementMethod.getInstance()
        binding.edWebsite.movementMethod = LinkMovementMethod.getInstance()

        binding.edEmailBP.isEnabled = false
        binding.edMobileNoBP.isEnabled = false

        binding.edCountry.setOnClickListener(this)
        binding.edState.setOnClickListener(this)
        binding.edCity.setOnClickListener(this)
        binding.btnSaveBP.setOnClickListener(this)
        binding.tvCategory.setOnClickListener(this)
        binding.addImageBP.setOnClickListener(this)
    }

    private fun getBusinessProfileApi(){
        if (Utility.hasConnection(this@BusinessProfile)) {
            val map: MutableMap<String, Any> = HashMap()
            map["token"] = ""+ token
            presenterAddBusiness.getBusinessProfileApi(this@BusinessProfile, map)
        }else {
            Utility.showToast(this@BusinessProfile, getString(R.string.no_network_message))
        }
    }

    private fun getCategoryList(){
        if (Utility.hasConnection(this@BusinessProfile)) {
            val map: MutableMap<String, Any> = HashMap()
            map["token"] = ""+ token
            presenterAddBusiness.getCategoryListApi(this@BusinessProfile, map)
        }else {
            Utility.showToast(this@BusinessProfile, getString(R.string.no_network_message))
        }
    }


    override fun onCategorySuccess(body: ModelCategory?) {
        val status = body!!.status
        categoryList.clear()
        if (status == 1){
            body.data?.let { categoryList.addAll(it) }
            dialogBinding= DataBindingUtil.inflate<PopupDialogLayoutBinding>(
                LayoutInflater.from(
                    this@BusinessProfile
                ),
                R.layout.popup_dialog_layout, binding.root as ViewGroup, false
            )
            dialogCategory= Dialog(Objects.requireNonNull(this@BusinessProfile))
            dialogCategory.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogCategory.setContentView(dialogBinding.root)
            Objects.requireNonNull(dialogCategory.window)!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialogCategory.show()

            categoryAdapter = AdapterCategory(this@BusinessProfile, categoryList, this)
            dialogBinding.recyclerView.layoutManager = LinearLayoutManager(this@BusinessProfile)
            dialogBinding.recyclerView.itemAnimator = DefaultItemAnimator()
            dialogBinding.recyclerView.adapter = categoryAdapter
            categoryAdapter.notifyDataSetChanged()
        }
        else if (status ==0){
            Utility.showToast(this@BusinessProfile, body.message)
        }
    }

    override fun onGetProfileSuccess(body: ModelGetBusiness?) {
        val status = body!!.status
        if (status == 1){
            if (body.data?.first_name!=null) {
                binding.edFirstNameBP.setText(body.data?.first_name)
            }
            if (body.data?.last_name!=null)
                binding.edLastNameBP.setText(body.data?.last_name)

            binding.edMobileNoBP.setText(body.data?.mobile)
            binding.edEmailBP.setText(body.data?.email)

            binding.edBusinessNameBP.setText(body.data?.user_detail?.business_name)
            binding.edDisplayNameBP.setText(body.data?.user_detail?.display_name)
            binding.edTaxNumberBP.setText(body.data?.user_detail?.business_tax_id)
            category_id = body.data?.user_detail?.category_id!!
            binding.tvCategory.setText( body.data?.user_detail?.category_name)

            binding.edWebsite.setText(body.data?.user_detail?.website)
            binding.edSocialMedia.setText(body.data?.user_detail?.social_media)

            if (body.data?.user_detail?.country_name!="" || body.data?.user_detail?.country !=null){
                countryId = body.data?.user_detail?.country?.toInt()!!
                binding.edCountry.setText(body.data?.user_detail?.country_name)
            }

            if (body.data?.user_detail?.state_name!="" || body.data?.user_detail?.state!=null){
                stateId = body.data?.user_detail?.state?.toInt()!!
                binding.edState.setText(body.data?.user_detail?.state_name)
            }

            if (body.data?.user_detail?.city_name!="" || body.data?.user_detail?.city!=null){
                cityId = body.data?.user_detail?.city?.toInt()!!
                binding.edCity.setText(body.data?.user_detail?.city_name)
            }

            if (body.data?.user_detail?.zipcode!=null){
                binding.edZipCode.setText(body.data?.user_detail?.zipcode)
            }

            if (body.data?.user_detail?.business_image!=null)
            Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.default_user_profile))
                .load(body.data?.user_detail?.business_image).into(binding.roundedImageView)

        }
    }

    override fun onUpdateBusinessProfileSuccess(body: ModelGetBusiness?) {
        val status = body!!.status
        if (status == 1){
            Toast.makeText(this@BusinessProfile, body.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }



    override fun onSelectedCategory(view: View?, index: Int, s: ModelCategory.DataBean) {
        category_id = s.id
        binding.tvCategory.setText(s.name)
        dialogCategory.dismiss()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.addImageBP ->{
                requestToUploadProfilePhoto()
            }
            R.id.tvCategory ->{
                Utility.hideKeyboard(this@BusinessProfile)
                getCategoryList()
            }
            R.id.edCountry ->{
                Utility.hideKeyboard(this@BusinessProfile)
                getCountryList()
            }
            R.id.edState ->{
                Utility.hideKeyboard(this@BusinessProfile)
                if (countryId!=0){
                    getStateList()
                }else{
                    Utility.shortToast(this@BusinessProfile, "Please select country")
                }
            }
            R.id.edCity ->{
                Utility.hideKeyboard(this@BusinessProfile)
                if (stateId!=0){
                    getCityList()
                }else{
                    Utility.shortToast(this@BusinessProfile, "Please select state")
                }
            }
            R.id.btnSaveBP ->{
                checkValidations()
            }
        }
    }

    private fun checkValidations() {
        if(TextUtils.isEmpty(binding.edFirstNameBP.text.toString().trim())) {
            Toast.makeText(this@BusinessProfile, "Please enter first name", Toast.LENGTH_SHORT).show()
            binding.edFirstNameBP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edLastNameBP.text.toString().trim())) {
            Toast.makeText(this@BusinessProfile, "Please enter last name", Toast.LENGTH_SHORT).show()
            binding.edLastNameBP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edBusinessNameBP.text.toString().trim())) {
            Toast.makeText(this@BusinessProfile, "Please enter Business name", Toast.LENGTH_SHORT).show()
            binding.edBusinessNameBP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edDisplayNameBP.text.toString().trim())) {
            Toast.makeText(this@BusinessProfile, "Please enter your business display name", Toast.LENGTH_SHORT).show()
            binding.edDisplayNameBP.requestFocus()
        }else if (TextUtils.isEmpty(binding.edTaxNumberBP.text.toString().trim())) {
            Toast.makeText(this@BusinessProfile, "Please enter business tax number", Toast.LENGTH_SHORT).show()
            binding.edTaxNumberBP.requestFocus()
        }/*else if (captureLicenseFile == null){
            Toast.makeText(this@AddBusiness, "Please select your shop/business license", Toast.LENGTH_SHORT).show()
        }else if (captureIdProofFile == null){
            Toast.makeText(this@AddBusiness, "Please select your id proof", Toast.LENGTH_SHORT).show()
        }*/else if (category_id == 0){
            Toast.makeText(this@BusinessProfile, "Please select category", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(binding.edCountry.text.toString().trim())){
            Toast.makeText(this@BusinessProfile, "Please select country", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(binding.edState.text.toString().trim())){
            Toast.makeText(this@BusinessProfile, "Please select state", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(binding.edCity.text.toString().trim())){
            Toast.makeText(this@BusinessProfile, "Please select city", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(binding.edZipCode.text.toString().trim())){
            Toast.makeText(this@BusinessProfile, "Please enter Zip/postal code", Toast.LENGTH_SHORT).show()
        }else{
            if (Utility.hasConnection(this@BusinessProfile)) {
                updateProfileApi()
            }else{
                Utility.showToast(this@BusinessProfile, getString(R.string.no_network_message))
            }
        }
    }

    private fun updateProfileApi() {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("token", token)
        builder.addFormDataPart("first_name", binding.edFirstNameBP.text.toString().trim())
        builder.addFormDataPart("last_name", binding.edLastNameBP.text.toString().trim())
        builder.addFormDataPart("business_name", binding.edBusinessNameBP.text.toString().trim())
        builder.addFormDataPart("display_name", binding.edDisplayNameBP.text.toString().trim())
        builder.addFormDataPart("business_tax_id", binding.edTaxNumberBP.text.toString().trim())
        //builder.addFormDataPart("about_business", binding.edAboutAB.text.toString().trim())
        builder.addFormDataPart("website", binding.edWebsite.text.toString().trim())
        builder.addFormDataPart("social_media", binding.edSocialMedia.text.toString().trim())
        builder.addFormDataPart("category_id", ""+category_id)
        builder.addFormDataPart("country",""+countryId)
        builder.addFormDataPart("state", ""+stateId )
        builder.addFormDataPart("city", ""+cityId )
        builder.addFormDataPart("zipcode", binding.edZipCode.text.toString().trim())
        /*val file1: File?
        if (captureLicenseFile != null) {
            try {
                file1 = FileUtil.from(this, captureLicenseFile)
                builder.addFormDataPart("license", Utility.getFileName(this, Uri.fromFile(file1)), RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), file1))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val file2: File?
        if (captureIdProofFile != null) {
            try {
                file2 = FileUtil.from(this, captureIdProofFile)
                builder.addFormDataPart("id_proof", Utility.getFileName(this, Uri.fromFile(file2)), RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), file2))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }*/
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
            builder!!.addFormDataPart("business_image", file.name, requestBody)
        }

        }*/
        if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            builder.addFormDataPart("business_image", file.name, requestBody)
        }
        val requestBody = builder.build()
        presenterAddBusiness.updateProfile(this@BusinessProfile, requestBody)
    }

    private fun getCountryList(){
        if (Utility.hasConnection(this@BusinessProfile)) {
            val map: MutableMap<String, Any> = HashMap()
            //map["token"] = ""+ token
            presenterAddBusiness.getCountryListApi(this@BusinessProfile, map)
        }else {
            Utility.showToast(this@BusinessProfile, getString(R.string.no_network_message))
        }
    }

    override fun onCountryListSuccess(body: ModelCountryList?) {
        val status = body!!.status
        countryList.clear()
        if (status == 1){
            body.data?.let { countryList.addAll(it) }
            countryDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(
                    this@BusinessProfile
                ),
                R.layout.popup_list_search_dialog_layout, binding.root as ViewGroup, false
            )
            countryDialog = Dialog(Objects.requireNonNull(this@BusinessProfile))
            countryDialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            countryDialog.setContentView(countryDialogBinding.root)
            Objects.requireNonNull(countryDialog.window)!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            countryDialog.show()
            countryDialogBinding.title.text = getString(R.string.select_country)
            countryDialogBinding.ivClose.setOnClickListener { countryDialog.dismiss() }

            countryAdapter = AdapterCountry(this@BusinessProfile, countryList, this)
            countryDialogBinding.recyclerView.layoutManager = LinearLayoutManager(this@BusinessProfile)
            countryDialogBinding.recyclerView.itemAnimator = DefaultItemAnimator()
            countryDialogBinding.recyclerView.adapter = countryAdapter
            countryAdapter.notifyDataSetChanged()

            countryDialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    filterCountryList(s.trim().toString(), countryList.toMutableList())
                }
            })
        }
        else if (status ==0){
            Utility.showToast(this@BusinessProfile, body.message)
        }
    }

    private fun filterCountryList(toString: String, countryModel: MutableList<ModelCountryList.DataBean>) {
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

    override fun onSelectedCountry(view: View?, index: Int, s: ModelCountryList.DataBean) {
        countryId = s.id
        edCountry.setText(s.name)
        countryDialog.dismiss()

        stateId = 0
        binding.edState.setText("")

        cityId = 0
        binding.edCity.setText("")
    }

    private fun getStateList(){
        if (Utility.hasConnection(this@BusinessProfile)) {
            val map: MutableMap<String, Any> = HashMap()
            map["country_id"] = ""+ countryId
            presenterAddBusiness.getStateListApi(this@BusinessProfile, map)
        }else {
            Utility.showToast(this@BusinessProfile, getString(R.string.no_network_message))
        }
    }

    override fun onStateListSuccess(body: ModelState?) {
        val status = body!!.status
        stateList.clear()
        if (status == 1){
            body.data?.let { stateList.addAll(it) }
            stateDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(
                    this@BusinessProfile
                ),
                R.layout.popup_list_search_dialog_layout, binding.root as ViewGroup, false
            )
            stateDialog = Dialog(Objects.requireNonNull(this@BusinessProfile))
            stateDialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            stateDialog.setContentView(stateDialogBinding.root)
            Objects.requireNonNull(stateDialog.window)!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            stateDialog.show()

            stateDialogBinding.title.text = getString(R.string.select_state)
            stateDialogBinding.ivClose.setOnClickListener { stateDialog.dismiss() }

            stateAdapter = AdapterState(this@BusinessProfile, stateList, this)
            stateDialogBinding.recyclerView.layoutManager = LinearLayoutManager(this@BusinessProfile)
            stateDialogBinding.recyclerView.itemAnimator = DefaultItemAnimator()
            stateDialogBinding.recyclerView.adapter = stateAdapter
            stateAdapter.notifyDataSetChanged()

            stateDialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    filterStateList(s.trim().toString(), stateList.toMutableList())
                }
            })
        }
        else if (status ==0){
            Utility.showToast(this@BusinessProfile, body.message)
        }

    }

    private fun filterStateList(toString: String, modelList: MutableList<ModelState.DataBean>) {
        var modelList1: MutableList<ModelState.DataBean> = modelList
        modelList1 = java.util.ArrayList<ModelState.DataBean>()
        for (item in stateList) {
            if (item.name?.toLowerCase(Locale.ROOT)!!.contains(
                    toString.toLowerCase(Locale.ROOT).toUpperCase(
                        Locale.ROOT
                    )
                )
                || item.name!!.toUpperCase(Locale.ROOT).contains(toString.toUpperCase(Locale.ROOT))) {
                modelList1.add(item)
            }
        }
        stateAdapter.filterStateList(modelList1)
    }

    override fun onSelectedState(view: View?, index: Int, s: ModelState.DataBean) {
        stateId = s.id
        binding.edState.setText(s.name)
        stateDialog.dismiss()

        cityId = 0
        binding.edCity.setText("")
    }

    private fun getCityList(){
        if (Utility.hasConnection(this@BusinessProfile)) {
            val map: MutableMap<String, Any> = HashMap()
            map["state_id"] = ""+ stateId
            presenterAddBusiness.getCityListApi(this@BusinessProfile, map)
        }else {
            Utility.showToast(this@BusinessProfile, getString(R.string.no_network_message))
        }
    }

    override fun onCityListSuccess(body: ModelCity?) {
        val status = body!!.status
        cityList.clear()
        if (status == 1){
            body.data?.let { cityList.addAll(it) }
            cityDialogBinding = DataBindingUtil.inflate<PopupListSearchDialogLayoutBinding>(
                LayoutInflater.from(
                    this@BusinessProfile
                ),
                R.layout.popup_list_search_dialog_layout, binding.root as ViewGroup, false
            )
            cityDialog = Dialog(Objects.requireNonNull(this@BusinessProfile))
            cityDialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            cityDialog.setContentView(cityDialogBinding.root)
            Objects.requireNonNull(cityDialog.window)!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            cityDialog.show()

            cityDialogBinding.title.text = getString(R.string.select_city)
            cityDialogBinding.ivClose.setOnClickListener { cityDialog.dismiss() }

            cityAdapter = AdapterCity(this@BusinessProfile, cityList, this)
            cityDialogBinding.recyclerView.layoutManager = LinearLayoutManager(this@BusinessProfile)
            cityDialogBinding.recyclerView.itemAnimator = DefaultItemAnimator()
            cityDialogBinding.recyclerView.adapter = cityAdapter
            cityAdapter.notifyDataSetChanged()

            cityDialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    filterCityList(s.trim().toString(), cityList.toMutableList())
                }
            })
        }
        else if (status ==0){
            Utility.showToast(this@BusinessProfile, body.message)
        }
    }

    private fun filterCityList(toString: String, modelList: MutableList<ModelCity.DataBean>) {
        var modelList1: MutableList<ModelCity.DataBean> = modelList
        modelList1 = java.util.ArrayList<ModelCity.DataBean>()
        for (item in cityList) {
            if (item.name?.toLowerCase(Locale.ROOT)!!.contains(
                    toString.toLowerCase(Locale.ROOT).toUpperCase(
                        Locale.ROOT
                    )
                )
                || item.name!!.toUpperCase(Locale.ROOT).contains(toString.toUpperCase(Locale.ROOT))) {
                modelList1.add(item)
            }
        }
        cityAdapter.filterCityList(modelList1)
    }

    override fun getContext(): Context {
        return this
    }

    override fun onSelectedCity(view: View?, index: Int, s: ModelCity.DataBean) {
        cityId = s.id
        edCity.setText(s.name)
        cityDialog.dismiss()
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
                    Toast.makeText(this, "something went wrong! please try again", Toast.LENGTH_SHORT).show()
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
                Glide.with(this).applyDefaultRequestOptions(RequestOptions()
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
        if (!hasPermissions(this@BusinessProfile, *PERMISSIONS_Photo)) {
            ActivityCompat.requestPermissions(this@BusinessProfile, PERMISSIONS_Photo, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(this@BusinessProfile, *PERMISSIONS_Photo)) {
            openCameraDialog()
        }
    }

    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel))
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
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
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
        val cursor = contentResolver.query(ur!!,
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
                    Toast.makeText(this, "Please grant both Camera and Storage permissions", Toast.LENGTH_SHORT).show()
                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(this, "Please grant both Camera and Storage permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }


}