package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.SignupModel
import com.teletap.model.VerifyOtpModel
import com.teletap.view.IVerifyView
import retrofit2.Callback
import retrofit2.Response

class VerifyOtpPresenter : BasePresenter<IVerifyView>(){

    fun verifyAccount(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.verifyAccount(map)?.enqueue(object : Callback<SignupModel?> {
            override fun onResponse(call: retrofit2.Call<SignupModel?>, response: Response<SignupModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onOtpSuccess(response.body())
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

    fun resendOtp(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.resendOtp(map)?.enqueue(object : Callback<VerifyOtpModel?> {
            override fun onResponse(call: retrofit2.Call<VerifyOtpModel?>, response: Response<VerifyOtpModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onResendSuccess(response.body())
                    }
                }
                else{
                    view.onError(response.message())
                }

            }
            override fun onFailure(call: retrofit2.Call<VerifyOtpModel?>, t: Throwable) {
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

    fun verifyLoginOtp(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.verifyLoginOtp(map)?.enqueue(object : Callback<SignupModel?> {
            override fun onResponse(call: retrofit2.Call<SignupModel?>, response: Response<SignupModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onVerifyLoginOtpSuccess(response.body())
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

    fun resendLoginOtp(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.resendLoginOtp(map)?.enqueue(object : Callback<VerifyOtpModel?> {
            override fun onResponse(call: retrofit2.Call<VerifyOtpModel?>, response: Response<VerifyOtpModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onResendLoginOtpSuccess(response.body())
                    }
                }
                else{
                    view.onError(response.message())
                }

            }
            override fun onFailure(call: retrofit2.Call<VerifyOtpModel?>, t: Throwable) {
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

    fun verifyForgetOtp(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.verifyOtp(map)?.enqueue(object : Callback<VerifyOtpModel?> {
            override fun onResponse(call: retrofit2.Call<VerifyOtpModel?>, response: Response<VerifyOtpModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onForgotPassOtpSuccess(response.body())
                    }
                }
                else{
                    view.onError(response.message())
                }

            }
            override fun onFailure(call: retrofit2.Call<VerifyOtpModel?>, t: Throwable) {
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

    fun resendForgetPassOtp(context:Context, map:MutableMap<String ,Any>) {
        view.enableLoadingBar(context,true,"Please Wait")
        AppController.getInstance().apiInterface.resendForgotOtp(map)?.enqueue(object : Callback<VerifyOtpModel?> {
            override fun onResponse(call: retrofit2.Call<VerifyOtpModel?>, response: Response<VerifyOtpModel?>) {
                view.enableLoadingBar(context,false,"")
                if (!handleError(response)){
                    if (response.isSuccessful && response.code()==200){
                        view.onResendForgotPassOtp(response.body())
                    }
                }
                else{
                    view.onError(response.message())
                }

            }
            override fun onFailure(call: retrofit2.Call<VerifyOtpModel?>, t: Throwable) {
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