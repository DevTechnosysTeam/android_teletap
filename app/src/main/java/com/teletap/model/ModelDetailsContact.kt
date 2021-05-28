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
    }
}