package com.teletap.view


import com.teletap.model.ModelContactList
import com.teletap.model.ModelShareInvoice


interface IContactListView : IView {
    fun onSuccess(body: ModelContactList?)

    fun onShareInvoiceSuccess(body : ModelShareInvoice?)
}