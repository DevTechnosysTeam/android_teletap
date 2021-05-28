package com.teletap.model

class ModelGetBusiness {
    /*{
        "status": 1,
        "dataflow": 1,
        "message": "Data Found successfully",
        "data": {
        "id": 59,
        "first_name": "Bhawana",
        "last_name": "Saini",
        "email": "b1@getnada.com",
        "country_code": "ss.anoop.aw",
        "mobile": "10203040",
        "profile_image": "no_image.jpg",
        "email_otp": 0,
        "sms_otp": 1234,
        "role": "users",
        "device_type": 1,
        "device_token": "dGfofCDAQI2qLFbGErvnIE:APA91bFV2Wy13acVAa7pUxPmlwPKxV_jmCwsh28G0X_aqgxVeBp5ep-jcLKHCTYgAw4H_JWqaMJs0miCTr9u3aAMcIt9M37a4rHnYhHi0sX_o6gJcYcpmzihIn99zNmIYFVn_Pwqcaxh",
        "apple_token": null,
        "google_token": null,
        "business_setup_status": 0,
        "sms_verified_status": 1,
        "reset_password_token": null,
        "status": 1,
        "created_at": "2021-05-13T10:55:21.000000Z",
        "updated_at": "2021-05-13T11:02:10.000000Z",
        "user_detail": {
        "id": 9,
        "user_id": 59,
        "business_name": null,
        "display_name": null,
        "about_business": "",
        "license": null,
        "business_image": null,
        "id_proof": null,
        "category_id": null,
        "website": "",
        "social_media": null,
        "country": null,
        "created_at": "2021-05-13T10:55:21.000000Z",
        "updated_at": "2021-05-13T10:55:21.000000Z"
    }
    }
    }*/
    val status = 0
    val dataflow = 0
    val message: String? = null
    var data: DataBean? = null

    class DataBean {

        var id = 0
        var status = 0
        var first_name: String = ""
        var last_name: String? = null
        var email: String? = null
        var profile_image: String? = null
        var mobile: String? = null
        var country_code: String? = null
        var business_setup_status = -1

        var user_detail: userDetailBean? = null

        class userDetailBean {
            var id = 0
            var user_id = 0
            var category_id = 0
            var business_name: String? = null
            var display_name: String? = null
            var about_business: String? = null
            var license: String? = null
            var updated_at: String? = null
            var business_image: String? = null
            var id_proof: String? = null
            var website: String? = null
            var social_media: String? = null
            var country: String? = null
            var state: String? = null
            var city: String? = null
            var zipcode: String? = null
            var category_name: String? = null
            var country_name: String? = null
            var state_name: String? = null
            var city_name: String? = null
        }
    }

}