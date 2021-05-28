package com.teletap.model

class ModelForgetPass {

    var status = 0
    var message: String? = null
    var error: ErrorBean? = null
    var data: DataBean? = null

    class ErrorBean
    class DataBean {
        var mobile: String? = null
        var email: String? = null
        var user_id: Int =0
    }
}