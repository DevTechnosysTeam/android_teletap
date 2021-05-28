package com.teletap.view


import com.teletap.model.ModelDetailsContact
import com.teletap.model.SimpleModel

interface IContactDetailView : IView {

    fun onDeleteSuccess(body: SimpleModel?)

    fun onSuccessDetails(body: ModelDetailsContact?)
}