package com.teletap.view

import com.teletap.model.ModelForgetPass
import com.teletap.model.ProfileModel
import com.teletap.model.SimpleModel

interface IHomeView : IView {

    fun onLogoutSuccess(body: SimpleModel?)
    fun onProfileSuccess(body: ProfileModel?)

}