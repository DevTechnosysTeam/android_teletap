package com.teletap.model

class DetailsPayoutModel {
    /**
    {
    "status": 1,
    "dataflow": 1,
    "message": "Data Found successfully",
    "data": {
    "id": 19,
    "payment_type": 1,
    "user_id": 69,
    "transaction_id": "9L9319652P195621K",
    "transaction_id_admin": null,
    "vendor_amount": 95,
    "admin_amount": 10,
    "tax": 5,
    "currency_exchange_percent": 0,
    "currency_type": "AED",
    "description": null,
    "status": 2,
    "created": "2021-06-04 18:36:49",
    "business_name": "poiu",
    "business_tax_id": null,
    "display_name": "yhnj",
    "about_business": null,
    "license": null,
    "business_image": "https://teletap.devtechnosys.info/public/uploads/business_image/54285.png",
    "id_proof": null,
    "category_id": 6,
    "website": null,
    "social_media": "https://www.google.com/search?q=google&oq=google&aqs=chrome..69i57j69i60l5.5803j0j4&client=ms-unknown&sourceid=chrome-mobile&ie=UTF-8",
    "country": "101",
    "state": "33",
    "city": "6068",
    "zipcode": "302017",
    "created_at": "2021-05-18 22:53:48",
    "updated_at": "2021-05-31 13:17:38"
    }
    }
     */
    var status = 0
    var message: String? = null
    var data: DataBean? = null

    class DataBean {

        var user_id: Int =0
        var id: Int =0
        var payment_type: Int =0
        var transaction_id :String? = null
        var transaction_id_admin :String? = null
        var vendor_amount : Float? = null
        var admin_amount : Float? = null
        var currency_exchange_percent : Float? = null
        var tax : Float? = null
        var currency_type : String? = null
        var description : String? = null
        var created : String? = null
        var business_name : String? = null
        var display_name : String? = null
        var about_business : String? = null
        var license : String? = null
        var business_image : String? = null
        var id_proof : String? = null
        var website : String? = null
        var social_media : String? = null
        var country : String? = null
        var state : String? = null
        var city : String? = null
        var zipcode : String? = null
        var created_at : String? = null
        var updated_at : String? = null
        var created_date : String? = null
        var created_time : String? = null
        var status : Int = -1
        var category_id : Int = -1

    }
}