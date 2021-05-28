package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.SimpleModel
import com.teletap.view.IContactUsView
import retrofit2.Callback
import retrofit2.Response

class ContactUsPresenter : BasePresenter<IContactUsView>() {

    fun callContactUsApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.contactUs(map)
            ?.enqueue(object : Callback<SimpleModel?> {
                override fun onResponse(
                    call: retrofit2.Call<SimpleModel?>,
                    response: Response<SimpleModel?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onSuccess(response.body())
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