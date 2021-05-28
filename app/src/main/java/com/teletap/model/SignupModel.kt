package com.teletap.model

class SignupModel {
    /**
    {"status":1,"dataflow":1,"message":"You are successfully registered.","data":{"user_id":39,"email":"q@as.asd","mobile":"147852741"}}
    "id":68,
    "first_name":"Dev",
    "last_name":"Techno",
    "email":"testingbydev@gmail.com",
    "country_code":"",
    "mobile":"123456789",
    "profile_image":"no_image.jpg",
    "email_otp":0,
    "sms_otp":1234,
    "role":"users",
    "device_type":0,
    "device_token":"a3d96a79b66288250f4cc837e73500f5024c4d702071156f55e54c01684f41bc",
    "apple_token":null,
    "google_token":"108114334992311882189",
    "business_setup_status":1,
    "sms_verified_status":1,
    "reset_password_token":null,
    "status":1,
    "created_at":"2021-05-18T11:03:10.000000Z",
    "updated_at":"2021-05-18T12:23:29.000000Z",
    "token"
     */
    var status = 0
    var message: String? = null
    var error: ErrorBean? = null
    var data: DataBean? = null

    class ErrorBean
    class DataBean {
        /**
         * phone : 77289905154
         * email : vs01@getnada.com
         */
        var mobile: String? = null
        var token: String? = null
        var email: String? = null
        var country_code: String? = null
        var first_name: String? = null
        var last_name: String? = null
        var id: Int =0
        var user_id: Int =0
        var business_setup_status: Int = -1
        var sms_verified_status: Int = -1

        /*var profile_setup: Int =0
        var interest_setup: Int =0
        var question_setup: Int =0
        var is_new : Int = -1*/

    }
}