package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.SignupModel
import com.teletap.model.VerifyOtpModel
import com.teletap.view.ILoginView
import com.teletap.view.IVerifyView
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter : BasePresenter<ILoginView>(){

    fun emailLogin(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.login(map)?.enqueue(object : Callback<SignupModel?> {
            override fun onResponse(call: retrofit2.Call<SignupModel?>, response: Response<SignupModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onSuccessEmail(response.body())
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

    fun numberLogin(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.loginWithNumber(map)?.enqueue(object : Callback<SignupModel?> {
            override fun onResponse(call: retrofit2.Call<SignupModel?>, response: Response<SignupModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onNumberLogin(response.body())
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