package com.teletap.presenterClasses

import android.content.Context
import com.teletap.BasePresenter
import com.teletap.api.AppController
import com.teletap.model.ModelContactList
import com.teletap.model.ModelShareInvoice
import com.teletap.model.SimpleModel
import com.teletap.view.IAddContactView
import com.teletap.view.IContactListView
import com.teletap.view.IContactUsView
import retrofit2.Callback
import retrofit2.Response

class ContactListPresenter : BasePresenter<IContactListView>() {

    fun getContactListApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.getContactList(map)
            ?.enqueue(object : Callback<ModelContactList?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelContactList?>,
                    response: Response<ModelContactList?>
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

                override fun onFailure(call: retrofit2.Call<ModelContactList?>, t: Throwable) {
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

    fun shareInvoiceApi(context: Context, map: MutableMap<String, Any>) {
        view.enableLoadingBar(context, true, "Please Wait")
        AppController.getInstance().apiInterface.shareContact(map)
            ?.enqueue(object : Callback<ModelShareInvoice?> {
                override fun onResponse(
                    call: retrofit2.Call<ModelShareInvoice?>,
                    response: Response<ModelShareInvoice?>
                ) {
                    view.enableLoadingBar(context, false, "")
                    if (!handleError(response)) {
                        if (response.isSuccessful && response.code() == 200) {
                            view.onShareInvoiceSuccess(response.body())
                        }
                    } else {
                        view.onError(response.message())
                    }

                }

                override fun onFailure(call: retrofit2.Call<ModelShareInvoice?>, t: Throwable) {
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