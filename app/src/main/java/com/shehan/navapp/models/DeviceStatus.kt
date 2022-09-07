package com.shehan.navapp.models

data class DeviceStatus(
    val pressure_top :Int,
    val battery_val : Int,
    val pressure_mid : Int,
    val unidentified_1 : Int,
    val pressure_low : Int,
    val pwm_top : Int,
    val pwm_mid : Int,
    val pwm_low : Int,
    val keep_work_time : Int,
    val ap_work_mode : Int,
    val intensity_flag : Int,
    val mode_step : Int,
    val step_time : Int,
    val pause_flag : Int,
)
