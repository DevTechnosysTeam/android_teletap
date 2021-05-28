package com.teletap.view

import com.teletap.model.ModelEarningsHome

interface IHomeEarningsView : IView {

    fun onSuccess(body: ModelEarningsHome?)
}