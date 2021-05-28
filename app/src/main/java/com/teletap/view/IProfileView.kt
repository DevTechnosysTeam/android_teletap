package com.teletap.view

import com.teletap.model.ProfileModel

interface IProfileView : IView {

    fun onProfileSuccess(body: ProfileModel?)

    fun onUpdateProfileSuccess(body: ProfileModel?)
}