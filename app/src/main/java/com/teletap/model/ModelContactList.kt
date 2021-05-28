package com.teletap.model

class ModelContactList {

    var status = 0
    var dataflow = 0
    var message: String? = null
    var data: List<DataBean>? = null

    class DataBean {
        /*"id": 5,
        "name": "",
        "email": "kids@mailinator.com",
        "country_code": 91,
        "mobile": "8844112233",
        "status": 1,
        "created_at": null,
        "updated_at": null*/

        var id = 0
        var status = 0
        var country_code : Int? = 0
        var name: String? = null
        var email: String? = null
        var created_at: String? = null
        var updated_at: String? = null
    }
}