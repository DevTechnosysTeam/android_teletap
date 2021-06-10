package com.teletap.model

class ProfileModel {
    /**
    {
    "status":1,
    "dataflow":1,
    "message":"Data Found successfully",
    "data":{
    "id":69,
    "first_name":"zxcv",
    "last_name":"qwer",
    "email":"d@getnada.com",
    "paypal_email":"sb-343bf94182375@business.example.com",
    "country_code":"+91",
    "mobile":"2020202020",
    "profile_image":"https:\/\/teletap.devtechnosys.info\/public\/uploads\/user\/62207.jpg",
    "email_otp":0,
    "sms_otp":1234,
    "role":"users",
    "device_type":1,
    "device_token":"eV4PnCTXRku43zJip0z-rh:APA91bGT_N_6zRXrQirATocDES2LST2GC92zuSHckJLwOdiz8JMj43t_G2SpWqM6WWqfIJe6v2li4vAE_yAY22qfib7mmYo0I0TNReXdupC3ua1VHdBvKPq2iHu8bqENdLuT8-tIfP42",
    "apple_token":null,
    "google_token":null,
    "business_setup_status":1,
    "sms_verified_status":1,
    "reset_password_token":null,
    "status":1,
    "created_at":"2021-05-18T17:23:48.000000Z",
    "updated_at":"2021-05-31T13:09:11.000000Z"
    }
    }
     */
    var status = 0
    var message: String? = null
    var error: ErrorBean? = null
    var data: DataBean? = null

    class ErrorBean
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

        /*var profile_setup: Int =0
        var interest_setup: Int =0
        var question_setup: Int =0
        var is_new : Int = -1*/

    }
}