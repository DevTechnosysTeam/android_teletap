package com.teletap.model

class ModelCity {

    val status = 0
    val dataflow = 0
    val message: String? = null
    var data: List<DataBean>? = null

    class DataBean {
        /**
         * id : 1
         * name : 1-school
         */
        var id = 0
        var state_id = 0
        var name: String? = null
    }
}