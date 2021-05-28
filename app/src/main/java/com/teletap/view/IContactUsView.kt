package com.teletap.view


import com.teletap.model.SimpleModel

interface IContactUsView : IView {
    fun onSuccess(body: SimpleModel?)
}