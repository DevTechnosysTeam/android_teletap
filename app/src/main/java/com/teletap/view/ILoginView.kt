package com.teletap.view

import com.teletap.model.SignupModel


interface ILoginView : IView {
    fun onSuccessEmail(body: SignupModel?)
    fun onNumberLogin(body: SignupModel?)
}