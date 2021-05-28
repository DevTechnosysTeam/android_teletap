package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.ModelGetBusiness
import com.teletap.model.ProfileModel
import com.teletap.view.IHomeView
import com.teletap.view.IProfileView
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

class ProfilePresenter : BasePresenter<IProfileView>() {

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

    fun updateProfile(context: Context, @Body file: RequestBody) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.updateProfile(file)?.enqueue(object : Callback<ProfileModel?> {
            override fun onResponse(call: Call<ProfileModel?>, response: Response<ProfileModel?>) {
                view!!.enableLoadingBar(context, false, "")
                if (!handleError(response)) {
                    if (response.isSuccessful() && response.code() == 200) {
                        view!!.onUpdateProfileSuccess(response.body())
                    }
                } else {
                    view!!.onError(response.message())
                }
            }

            override fun onFailure(call: Call<ProfileModel?>, t: Throwable) {
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

}