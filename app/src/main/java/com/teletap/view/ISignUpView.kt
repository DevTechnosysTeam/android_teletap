package com.teletap.view

import com.teletap.model.SignupModel


interface ISignUpView : IView {
    fun onSuccess(body: SignupModel?)
    fun onGoogleLogin(body: SignupModel?)
}