package com.teletap.model

class PaymentCardModel {
    /**
    {
    "status":1,
    "dataflow":1,
    "message":"Payment has been done successfully.",
    "data":{
    "transaction_id":"8FX33395B48307432",
    "payment_type":1,
    "user_id":69,
    "vendor_amount":91,
    "admin_amount":10,
    "tax":null,
    "currency_exchange_percent":"1.0",
    "status":2,
    "currency_type":"USD",
    "description":"thank you for your time and consideration and"
    }
    }
     */
    var status = 0
    var dataflow = 0
    var message: String? = null
    var error: ErrorBean? = null
    var data: DataBean? = null
    class ErrorBean

    class DataBean(){
        var transaction_id: String? = null
        var payment_type = 0
        var user_id = 0
        var vendor_amount:Float = 0.0f
        var admin_amount:Float = 0.0f
        var tax:Float = 0.0f
        var currency_exchange_percent: String? = null
        var status = 0
        var currency_type: String? = null
        var description: String? = null
    }

}