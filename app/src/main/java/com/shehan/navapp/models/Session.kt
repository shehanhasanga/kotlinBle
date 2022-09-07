package com.shehan.navapp.models

data class Session(
    val sessionId: String,
    var elapseTime : Int? = 0,
    val deviceIdAndroid : String,
    val deviceIdIos : String,
    var totalTime : Int?,
    var therapyList : Array<TherapyConfig>
){

}
