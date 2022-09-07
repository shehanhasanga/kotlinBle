package com.shehan.navapp.models

data class TherapyConfig(
    val pattern : Int,
    val itensity : Int,
    val time : Int,
    var progress : Int? = 0
)
