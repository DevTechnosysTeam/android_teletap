package com.teletap.view

import com.teletap.model.ModelForgetPass

interface IForgotPassView : IView {
    fun onSuccess(body: ModelForgetPass?)

}