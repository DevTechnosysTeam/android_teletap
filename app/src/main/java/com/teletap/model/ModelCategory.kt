package com.teletap.model

class ModelCategory {

    /*{
        "status": 1,
        "dataflow": 1,
        "message": "Data Found successfully.",
        "data": [
        {
            "id": 1,
            "name": "cat 1",
            "name_ar": "cate 1",
            "status": 1,
            "created_at": "2021-05-12 12:42:21",
            "updated_at": "2021-05-20 12:42:24"
        }
        ]
    }*/

    val status = 0
     val dataflow = 0
     val message: String? = null
    var data: List<DataBean>? = null

    class DataBean {
        /**
         * school_id : 1
         * name : 1-school
         */
        var id = 0
        var status = 0
        var name: String? = null
        var name_ar: String? = null
        var created_at: String? = null
        var updated_at: String? = null
    }
}