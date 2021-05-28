package com.teletap.view


import com.teletap.model.SimpleModel

interface IResetPassView : IView {
    fun onSuccess(body: SimpleModel?)

}