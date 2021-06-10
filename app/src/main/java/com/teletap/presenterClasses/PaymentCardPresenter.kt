package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.PaymentCardModel
import com.teletap.model.SimpleModel
import com.teletap.view.IContactUsView
import com.teletap.view.IPaymentCardView
import retrofit2.Callback
import retrofit2.Response

class PaymentCardPresenter : BasePresenter<IPaymentCardView>() {

    fun doCardPaymentApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.doCardPayment(map)
            ?.enqueue(object : Callback<PaymentCardModel?> {
                override fun onResponse(
                    call: retrofit2.Call<PaymentCardModel?>,
                    response: Response<PaymentCardModel?>
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

                override fun onFailure(call: retrofit2.Call<PaymentCardModel?>, t: Throwable) {
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