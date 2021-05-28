package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.ModelEarningsHome
import com.teletap.model.ProfileModel
import com.teletap.view.IHomeEarningsView
import retrofit2.Callback
import retrofit2.Response


class EarningHomePresenter : BasePresenter<IHomeEarningsView>() {

    fun getEarningList(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getHomeList(map)
            ?.enqueue(object : Callback<ModelEarningsHome?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelEarningsHome?>,
                    response: Response<ModelEarningsHome?>
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

                override fun onFailure(call: retrofit2.Call<ModelEarningsHome?>, t: Throwable) {
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