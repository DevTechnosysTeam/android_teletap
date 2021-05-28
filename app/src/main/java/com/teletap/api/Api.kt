package com.teletap.api

import com.teletap.model.*
import com.teletap.utilities.LanguagePreference
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //private var language : String = LanguagePreference().getLanguageType().toString()
    companion object {

          //const val BASE_URL =    "https://thaisweetheart.com/api/"
          const val BASE_URL =    "https://teletap.devtechnosys.info/api/webservices/"

    }

    @FormUrlEncoded
    @POST("register")
    fun register(@FieldMap partMa: MutableMap<String, Any>): Call<SignupModel?>?

    @FormUrlEncoded
    @POST("googleLogin")
    fun googleLogin(@FieldMap partMa: MutableMap<String, Any>): Call<SignupModel?>?

    @FormUrlEncoded
    @POST("verifyAccount")
    fun verifyAccount(@FieldMap partMa: MutableMap<String, Any>): Call<SignupModel?>?

     @FormUrlEncoded
    @POST("resendOtp")
    fun resendOtp(@FieldMap partMa: MutableMap<String, Any>): Call<VerifyOtpModel?>?

    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap partMa: MutableMap<String, Any>): Call<SignupModel?>?

    @FormUrlEncoded
    @POST("loginWithNumber")
    fun loginWithNumber(@FieldMap partMa: MutableMap<String, Any>): Call<SignupModel?>?

    @FormUrlEncoded
    @POST("verifyLoginOtp")
    fun verifyLoginOtp(@FieldMap partMa: MutableMap<String, Any>): Call<SignupModel?>?

    @FormUrlEncoded
    @POST("resendLoginOtp")
    fun resendLoginOtp(@FieldMap partMa: MutableMap<String, Any>): Call<VerifyOtpModel?>?

     @FormUrlEncoded
    @POST("forgotPassword")
    fun forgotPassword(@FieldMap partMa: MutableMap<String, Any>): Call<ModelForgetPass?>?

    @FormUrlEncoded
    @POST("verifyOtp")
    fun verifyOtp(@FieldMap partMa: MutableMap<String, Any>): Call<VerifyOtpModel?>?

    @FormUrlEncoded
    @POST("resendForgotOtp")
    fun resendForgotOtp(@FieldMap partMa: MutableMap<String, Any>): Call<VerifyOtpModel?>?

    @FormUrlEncoded
    @POST("resetPassword")
    fun resetPassword(@FieldMap partMa: MutableMap<String, Any>): Call<SimpleModel?>?

    @FormUrlEncoded
    @POST("logout")
    fun logout(@FieldMap partMa: MutableMap<String, Any>): Call<SimpleModel?>?

    @FormUrlEncoded
    @POST("getUserProfile")
    fun getUserProfile(@FieldMap partMa: MutableMap<String, Any>): Call<ProfileModel?>?

    @FormUrlEncoded
    @POST("getCategoryList")
    fun getCategoryList(@FieldMap partMa: MutableMap<String, Any>): Call<ModelCategory?>?

    @FormUrlEncoded
    @POST("getBusinessProfile")
    fun getBusinessProfile(@FieldMap partMa: MutableMap<String, Any>): Call<ModelGetBusiness?>?

    @POST("updateBusinessProfile")
    fun updateBusinessProfile(@Body file:RequestBody): Call<ModelGetBusiness?>?

    @POST("updateProfile")
    fun updateProfile(@Body file:RequestBody): Call<ProfileModel?>?

    @FormUrlEncoded
    @POST("getHomeList")
    fun getHomeList(@FieldMap partMa: MutableMap<String, Any>): Call<ModelEarningsHome?>?

    @FormUrlEncoded
    @POST("contact_us")
    fun contactUs(@FieldMap partMa: MutableMap<String, Any>): Call<SimpleModel?>?

    @FormUrlEncoded
    @POST("getCountryList")
    fun getCountryList(@FieldMap partMa: MutableMap<String, Any>): Call<ModelCountryList?>?

    @FormUrlEncoded
    @POST("getStateList")
    fun getStateList(@FieldMap partMa: MutableMap<String, Any>): Call<ModelState?>?

    @FormUrlEncoded
    @POST("getCityList")
    fun getCityList(@FieldMap partMa: MutableMap<String, Any>): Call<ModelCity?>?

    @FormUrlEncoded
    @POST("addContact")
    fun addContact(@FieldMap partMa: MutableMap<String, Any>): Call<SimpleModel?>?

    @FormUrlEncoded
    @POST("getContactList")
    fun getContactList(@FieldMap partMa: MutableMap<String, Any>): Call<ModelContactList?>?

    @FormUrlEncoded
    @POST("contactDetails")
    fun contactDetails(@FieldMap partMa: MutableMap<String, Any>): Call<ModelDetailsContact?>?

    @FormUrlEncoded
    @POST("deleteContact")
    fun deleteContact(@FieldMap partMa: MutableMap<String, Any>): Call<SimpleModel?>?

    @FormUrlEncoded
    @POST("updateContact")
    fun updateContact(@FieldMap partMa: MutableMap<String, Any>): Call<SimpleModel?>?

    /*@FormUrlEncoded
    @POST("profile/home")
    fun home(@FieldMap partMa: MutableMap<String, Any>): Call<ModelHome?>?*/

    /*@FormUrlEncoded
    @POST("option/country")
    fun country(@FieldMap partMa: MutableMap<String, Any>): Call<ModelCountry?>?*/

    /*@FormUrlEncoded
    @POST("option/state")
    fun state(@FieldMap partMa: MutableMap<String, Any>): Call<ModelState?>?*/

}