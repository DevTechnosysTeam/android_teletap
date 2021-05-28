package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.SignupModel
import com.teletap.view.ISignUpView
import retrofit2.Callback
import retrofit2.Response

class SignupPresenter : BasePresenter<ISignUpView>(){

    fun register(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.register(map)?.enqueue(object : Callback<SignupModel?> {
            override fun onResponse(call: retrofit2.Call<SignupModel?>, response: Response<SignupModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onSuccess(response.body())
                    }
                }
                else{
                        view.onError(response.message())
                }

            }
            override fun onFailure(call: retrofit2.Call<SignupModel?>, t: Throwable) {
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


    fun googleLogin(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.googleLogin(map)?.enqueue(object : Callback<SignupModel?> {
            override fun onResponse(call: retrofit2.Call<SignupModel?>, response: Response<SignupModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onGoogleLogin(response.body())
                    }
                }
                else{
                    view.onError(response.message())
                }
            }
            override fun onFailure(call: retrofit2.Call<SignupModel?>, t: Throwable) {
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