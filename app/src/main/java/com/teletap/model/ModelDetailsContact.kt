package com.teletap.model

class ModelDetailsContact {

    var status = 0
    var dataflow = 0
    var message: String? = null
    var data:DataBean? = null


    class DataBean{
       /* "id": 9,
        "name": "Taruny",
        "email": "t@getnada.com",
        "country_code": 91,
        "mobile": "12345678",
        "status": 1,
        "created_at": null,
        "updated_at": null,
        "transaction_history": []*/

        var id = 0
        var status = 0
        var name: String? = null
        var email: String? = null
        var country_code: String? = null
        var mobile: String? = null
        var created_at: String? = null
        var updated_at: String? = null

        var transaction_history : List<TransactionHistoryBean>? = null

        class TransactionHistoryBean{
            /*"id":24,
            "contact_id":6,
            "transaction_id":"9VT87952GM800254N",
            "status":1,
            "created":"2021-05-31 15:11:54",
            "transaction_details":{
                "id":10,
                "payment_type":1,
                "user_id":60,
                "transaction_id":"9VT87952GM800254N",
                "transaction_id_admin":null,
                "vendor_amount":95,
                "admin_amount":10,
                "tax":5,
                "currency_exchange_percent":0,
                "currency_type":"AED",
                "description":null,
                "status":2,
                "created":"2021-05-29 18:57:23",
                "business_name":"Abhilasha Rocks before",
                "display_name":"Abhi",
                "about_business":"Fklsdnglknsgl\nVgdjksnkldnxvklsdzlv",
                "license":null,
                "business_image":"https:\/\/teletap.devtechnosys.info\/public\/uploads\/business_image\/40977.jpg",
                "id_proof":"https:\/\/teletap.devtechnosys.info\/public\/uploads\/id_proof\/74302.pdf",
                "category_id":6,
                "website":"https:\/\/www.google.com\/",
                "social_media":null,
                "country":"101",
                "state":"21",
                "city":"2214",
                "zipcode":"101010",
                "created_at":"2021-05-14 13:56:36",
                "updated_at":"2021-05-26 10:41:31"*/
            var id = 0
            var contact_id = 0
            var transaction_id: String? = null
            var created : String? = null

            var transaction_details : TransactionDetailsBean? = null

            class TransactionDetailsBean(){
                var id = 0
                var payment_type = 0
                var user_id = 0
                var transaction_id:String? = null
                var transaction_id_admin:String? = null
                var vendor_amount : Double = 0.0
                var admin_amount : Double= 0.0
                var tax : Double= 0.0
                var currency_exchange_percent: Double = 0.0
                var currency_type : String? = null
                var description : String? = null
                var status = 0
                var created : String? = null
                var business_name : String? = null
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
        }
    }
}