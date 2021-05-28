package com.teletap.view

import com.teletap.model.*


interface IAddBusinessView  : IView{

    fun onCategorySuccess(body: ModelCategory?)

    fun onGetProfileSuccess(body: ModelGetBusiness?)

    fun onUpdateBusinessProfileSuccess(body: ModelGetBusiness?)

    fun onCountryListSuccess(body: ModelCountryList?)

    fun onStateListSuccess(body: ModelState?)

    fun onCityListSuccess(body: ModelCity?)
}