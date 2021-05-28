package com.teletap.model

class ProfileModel {
    /**
    {
    "status":1,
    "dataflow":1,
    "message":"Data Found successfully",
    "data":{
    "id":47,
    "first_name":"Bhawana",
    "last_name":"Saini",
    "email":"b@getnada.com",
    "country_code":"",
    "mobile":"9988776655",
    "profile_image":"no_image.jpg",
    "email_otp":9970,
    "sms_otp":1234,
    "role":"users",
    "device_type":1,
    "device_token":"dGfofCDAQI2qLFbGErvnIE:APA91b",
    "apple_token":null,
    "google_token":null,
    "business_setup_status":0,
    "sms_verified_status":1,
    "status":1,
    "created_at":"2021-05-05T13:35:36.000000Z",
    "updated_at":"2021-05-07T10:52:47.000000Z"
    }
    }
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

        var token: String? = null
        var email: String? = null
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