package com.teletap.model

class BankAccountModel {
    /**
    {
    "status":1,
    "dataflow":1,
    "message":"Account Verified successfully.",
    "data":{
    "id":69,
    "first_name":"zxcv",
    "last_name":"qwer",
    "email":"d@getnada.com",
    "paypal_email":"sb-343bf94182375@business.example.com",
    "country_code":"+91",
    "mobile":"2020202020",
    "profile_image":"62207.jpg",
    "email_otp":0,
    "sms_otp":1234,
    "role":"users",
    "device_type":1,
    "device_token":"czxoAs7QR5uDHl2GJRFYAl:APA91bGD7KQyPXdOm1txtGIJsZjYHtGJSIjUTIA-CVBLRldsFoK4PrkFNU7rWwCw3Kk1T-N4O-v8ysSB4Omey23ygJq8ajqT3G8PS19m1dlZzB-zBW9E-UCzUm2Z14Hv024sA1mGkAqj",
    "apple_token":null,
    "google_token":null,
    "business_setup_status":1,
    "sms_verified_status":1,
    "reset_password_token":null,
    "status":1,
    "created_at":"2021-05-18T17:23:48.000000Z",
    "updated_at":"2021-05-31T07:25:11.000000Z"
    }
    }
     */
    var status = 0
    var message: String? = null

    var data: DataBean? = null

    class DataBean {

        var token: String? = null
        var email: String? = null
        var paypal_email: String? = null
        var country_code: String? = null
        var mobile: String? = null
        var first_name: String? = null
        var last_name: String? = null
        var profile_image: String? = null
        var user_id: Int =0
        var id: Int =0
        var business_setup_status: Int = -1
        var sms_verified_status: Int = -1
    }
}