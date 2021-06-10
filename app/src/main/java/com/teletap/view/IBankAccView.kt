package com.teletap.view


import com.teletap.model.BankAccountModel
import com.teletap.model.SimpleModel


interface IBankAccView : IView {
    fun onSuccess(body: BankAccountModel?)
}