package com.teletap.view


import com.teletap.model.ModelDetailsContact
import com.teletap.model.SimpleModel

interface IAddContactView : IView {
    fun onSuccess(body: SimpleModel?)

    fun onEditSuccess(body: SimpleModel?)

    fun onSuccessDetails(body: ModelDetailsContact?)
}