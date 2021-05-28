package com.teletap.view


import com.teletap.model.ModelContactList


interface IContactListView : IView {
    fun onSuccess(body: ModelContactList?)
}