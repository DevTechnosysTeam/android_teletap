package com.teletap.model

class ModelEarningsHome {

    var status = 0
    var message: String? = null
    var data: DataBean? = null

    class DataBean {
        var total_earnings:String? = null
        var profile_detail : ProfileDetailBean? = null
        var earning_data: List<EarningDataBean>? = null

        class EarningDataBean{

            /*"id": 10,
            "payment_type": 2,
            "user_id": 60,
            "transaction_id": "#dsaas21321",
            "total_amount": 40,
            "status": 1,
            "created": "2021-05-18 17:25:28",
            "business_name": "Abhilasha Rocks before",
            "display_name": "Abhi",
            "about_business": "Fklsdnglknsgl\nVgdjksnkldnxvklsdzlv",
            "license": null,
            "business_image": "https://teletap.devtechnosys.info/public/uploads/business_image/46318.jpg",
            "id_proof": "https://teletap.devtechnosys.info/public/uploads/id_proof/74302.pdf",
            "category_id": 6,
            "website": "https://www.google.com/",
            "social_media": null,
            "country": "101",
            "state": "12",
            "city": "786",
            "zipcode": "101010",
            "created_at": "2021-05-14 13:56:36",
            "updated_at": "2021-05-19 13:54:36"*/

            var id = 0
            var payment_type = 0
            var user_id = 0
            var total_amount = 0
            var category_id = 0
            var transaction_id: String? = null
            var business_name: String? = null
            var display_name: String? = null
            var about_business: String? = null
            var license: String? = null
            var business_image: String? = null
            var id_proof: String? = null
            var social_media: String? = null
            var website: String? = null
            var country: String? = null
            var state: String? = null
            var city: String? = null
            var zipcode : String? = null
            var created : String? = null

        }

        class ProfileDetailBean{
            /*"id": 70,
            "first_name": "Dev",
            "last_name": "Techno",
            "email": "testingbydev@gmail.com",
            "country_code": "+91",
            "mobile": "7417417417",
            "profile_image": "https://teletap.devtechnosys.info/public/uploads/user/no_image.jpg",
            "email_otp": 0,
            "sms_otp": 1234,
            "role": "users",
            "device_type": 0,
            "device_token": "dGfofCDAQI2qLFbGErvnIE:APA91bFV2Wy13acVAa7pUxPmlwPKxV_jmCwsh28G0X_aqgxVeBp5ep-jcLKHCTYgAw4H_JWqaMJs0miCTr9u3aAMcIt9M37a4rHnYhHi0sX_o6gJcYcpmzihIn99zNmIYFVn_Pwqcaxh",
            "apple_token": null,
            "google_token": "108114334992311882189",
            "business_setup_status": 0,
            "sms_verified_status": 1,
            "reset_password_token": null,
            "status": 1,
            "created_at": "2021-05-19T09:57:52.000000Z",
            "updated_at": "2021-05-19T10:43:30.000000Z"*/
            var id = 0
            var first_name: String? = null
            var last_name: String? = null
            var email: String? = null
            var country_code: String? = null
            var mobile: String? = null
            var profile_image: String? = null
        }
    }
}