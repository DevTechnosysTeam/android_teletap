package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.ModelDetailsContact
import com.teletap.model.SimpleModel
import com.teletap.view.IAddContactView
import com.teletap.view.IContactDetailView
import com.teletap.view.IContactUsView
import retrofit2.Callback
import retrofit2.Response

class DetailsContactPresenter : BasePresenter<IContactDetailView>() {

    fun contactDetailsApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.contactDetails(map)
            ?.enqueue(object : Callback<ModelDetailsContact?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelDetailsContact?>,
                    response: Response<ModelDetailsContact?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onSuccessDetails(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ModelDetailsContact?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }

    fun deleteContactApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.deleteContact(map)
            ?.enqueue(object : Callback<SimpleModel?> {
                override fun onResponse(
                    call: retrofit2.Call<SimpleModel?>,
                    response: Response<SimpleModel?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onDeleteSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<SimpleModel?>, t: Throwable) {
                    view!!.enableLoadingBar(context, false, "")
                    try {
                        t.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    view!!.onError(t.message)
                }
            })
    }
}