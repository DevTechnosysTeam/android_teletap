package com.teletap.model

class ModelEarningsHome {

    var status = 0
    var message: String? = null
    var data: DataBean? = null

    class DataBean {
        /*"total_earnings":0,
        "currency_exchange_charge":3.67,
        "currency_exchange_percent":1*/

        var total_earnings:String? = null
        var currency_exchange_charge:Float? = null
        var currency_exchange_percent:Float? = null

        var profile_detail : ProfileDetailBean? = null
        var earning_data: List<EarningDataBean>? = null

        class EarningDataBean{

            /*"id":19,
                "payment_type":1,
                "user_id":69,
                "transaction_id":"9L9319652P195621K",
                "transaction_id_admin":null,
                "vendor_amount":95,
                "admin_amount":10,
                "tax":5,
                "currency_exchange_percent":0,
                "currency_type":"AED",
                "description":null,
                "status":2,
                "created":"2021-06-04 18:36:49",
                "business_name":"poiu",
                "business_tax_id":null,
                "display_name":"yhnj",
                "about_business":null,
                "license":null,
                "business_image":"https:\/\/teletap.devtechnosys.info\/public\/uploads\/business_image\/54285.png",
                "id_proof":null,
                "category_id":6,
                "website":null,
                "social_media":"https:\/\/www.google.com\/search?q=google&oq=google&aqs=chrome..69i57j69i60l5.5803j0j4&client=ms-unknown&sourceid=chrome-mobile&ie=UTF-8",
                "country":"101",
                "state":"33",
                "city":"6068",
                "zipcode":"302017",
                "created_at":"2021-05-18 22:53:48",
                "updated_at":"2021-05-31 13:17:38"*/

            var id = 0
            var payment_type = 0
            var user_id = 0
            var vendor_amount = 0.0f
            var admin_amount = 0.0f
            var tax = 0.0f
            var currency_exchange_percent = 0.0f
            var category_id = 0
            var transaction_id: String? = null
            var transaction_id_admin: String? = null
            var currency_type: String? = null
            var created: String? = null
            var description: String? = null
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
            var created_at : String? = null
            var updated_at : String? = null

            var created_date : String? = null
            var created_time : String? = null

        }

        class ProfileDetailBean{
            /*"id":69,
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
                "updated_at":"2021-05-31T13:09:11.000000Z"*/
            var id = 0
            var first_name: String? = null
            var last_name: String? = null
            var email: String? = null
            var paypal_email: String? = null
            var country_code: String? = null
            var mobile: String? = null
            var profile_image: String? = null
        }
    }
}