package com.teletap.view


import com.teletap.model.PaymentCardModel


interface IPaymentCardView : IView {
    fun onSuccess(body: PaymentCardModel?)
}