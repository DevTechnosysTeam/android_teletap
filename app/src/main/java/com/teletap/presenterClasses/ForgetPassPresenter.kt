package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.ModelForgetPass
import com.teletap.model.SignupModel
import com.teletap.view.IForgotPassView
import com.teletap.view.ISignUpView
import retrofit2.Callback
import retrofit2.Response

class ForgetPassPresenter : BasePresenter<IForgotPassView>() {

    fun forgetPassApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.forgotPassword(map)
            ?.enqueue(object : Callback<ModelForgetPass?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelForgetPass?>,
                    response: Response<ModelForgetPass?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ModelForgetPass?>, t: Throwable) {
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