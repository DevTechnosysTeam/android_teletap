package com.teletap.view


import com.teletap.model.SignupModel
import com.teletap.model.VerifyOtpModel


interface IVerifyView : IView {

    fun onOtpSuccess(body: SignupModel?)
    fun onResendSuccess(body: VerifyOtpModel?)

    fun onResendLoginOtpSuccess(body: VerifyOtpModel?)

    fun onVerifyLoginOtpSuccess(body: SignupModel?)

    fun onForgotPassOtpSuccess(body: VerifyOtpModel?)
    fun onResendForgotPassOtp(body: VerifyOtpModel?)
}