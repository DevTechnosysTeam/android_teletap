package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.*
import com.teletap.view.IAddBusinessView
import com.teletap.view.IForgotPassView
import com.teletap.view.ISignUpView
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

class AddBusiness_Presenter : BasePresenter<IAddBusinessView>() {

    fun getCategoryListApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getCategoryList(map)
            ?.enqueue(object : Callback<ModelCategory?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelCategory?>,
                    response: Response<ModelCategory?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onCategorySuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ModelCategory?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }

    fun getBusinessProfileApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getBusinessProfile(map)
            ?.enqueue(object : Callback<ModelGetBusiness?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelGetBusiness?>,
                    response: Response<ModelGetBusiness?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onGetProfileSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ModelGetBusiness?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }

    fun updateProfile(context: Context, @Body file: RequestBody) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.updateBusinessProfile(file)?.enqueue(object : Callback<ModelGetBusiness?> {
            override fun onResponse(call: Call<ModelGetBusiness?>, response: Response<ModelGetBusiness?>) {
                view!!.enableLoadingBar(context, false, "")
                if (!handleError(response)) {
                    if (response.isSuccessful() && response.code() == 200) {
                        view!!.onUpdateBusinessProfileSuccess(response.body())
                    }
                } else {
                    view!!.onError(response.message())
                }
            }

            override fun onFailure(call: Call<ModelGetBusiness?>, t: Throwable) {
                view!!.enableLoadingBar(context, false, "")
                try {
                    t.printStackTrace()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                view!!.onError(t.message)
            }
        })
    }

    fun getCountryListApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getCountryList(map)
            ?.enqueue(object : Callback<ModelCountryList?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelCountryList?>,
                    response: Response<ModelCountryList?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onCountryListSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }
                }
                override fun onFailure(call: retrofit2.Call<ModelCountryList?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }

    fun getStateListApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getStateList(map)
            ?.enqueue(object : Callback<ModelState?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelState?>,
                    response: Response<ModelState?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onStateListSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ModelState?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }

    fun getCityListApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getCityList(map)
            ?.enqueue(object : Callback<ModelCity?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelCity?>,
                    response: Response<ModelCity?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onCityListSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }
                }

                override fun onFailure(call: retrofit2.Call<ModelCity?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }

}