package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.ModelForgetPass
import com.teletap.model.ProfileModel
import com.teletap.model.SignupModel
import com.teletap.model.SimpleModel
import com.teletap.view.IForgotPassView
import com.teletap.view.IHomeView
import com.teletap.view.ISignUpView
import retrofit2.Callback
import retrofit2.Response

class HomePresenter : BasePresenter<IHomeView>() {

    fun logoutApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.logout(map)
            ?.enqueue(object : Callback<SimpleModel?> {
                override fun onResponse(
                    call: retrofit2.Call<SimpleModel?>,
                    response: Response<SimpleModel?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onLogoutSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<SimpleModel?>, t: Throwable) {
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

    fun getUserProfile(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getUserProfile(map)
            ?.enqueue(object : Callback<ProfileModel?> {
                override fun onResponse(
                    call: retrofit2.Call<ProfileModel?>,
                    response: Response<ProfileModel?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onProfileSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ProfileModel?>, t: Throwable) {
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