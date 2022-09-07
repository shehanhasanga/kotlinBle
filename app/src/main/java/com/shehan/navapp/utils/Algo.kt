package com.shehan.navapp.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Algo {
    companion object {
        var sensorDF = DecimalFormat("0.00")
        var distanceDF = DecimalFormat("0.0")
         val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
         val randomGenerator = Random()

        const val GRADUATED_MODE = 0
        const val PULSATED_MODE = 1

         val uniqueID: String? = null
         const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

        // Array indexing - [intensity][mode][sensorIndex]
         var m = arrayOf(
            arrayOf(doubleArrayOf(1.62, 1.775, 1.758), doubleArrayOf(1.62, 1.775, 1.758)),
            arrayOf(doubleArrayOf(1.62, 1.775, 1.758), doubleArrayOf(1.62, 1.775, 1.758)),
            arrayOf(doubleArrayOf(1.62, 1.775, 1.758), doubleArrayOf(1.62, 1.775, 1.758))
        )
         var c = Array(3) {
            Array(2) {
                DoubleArray(3)
            }
        }


         const val max_val = 110.0
         var max_mmhg = arrayOf(
            arrayOf(doubleArrayOf(30.0, 40.0, 50.0), doubleArrayOf(40.0, 50.0, 70.0)),
            arrayOf(doubleArrayOf(60.0, 70.0, 80.0), doubleArrayOf(50.0, 70.0, 90.0)),
            arrayOf(doubleArrayOf(90.0, 100.0, 110.0), doubleArrayOf(70.0, 90.0, 110.0))
        )
         const val min_val = 20.0
         var min_mmhg = arrayOf(
            arrayOf(doubleArrayOf(20.0, 20.0, 20.0), doubleArrayOf(20.0, 20.0, 19.0)),
            arrayOf(doubleArrayOf(20.0, 20.0, 20.0), doubleArrayOf(20.0, 20.0, 20.0)),
            arrayOf(doubleArrayOf(20.0, 20.0, 20.0), doubleArrayOf(20.0, 20.0, 19.0))
        )


         var max_x = arrayOf(
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 0, 0))
        )
         var min_x = arrayOf(
            arrayOf(intArrayOf(65536, 65536, 65536), intArrayOf(65536, 65536, 65536)),
            arrayOf(intArrayOf(65536, 65536, 65536), intArrayOf(65536, 65536, 65536)),
            arrayOf(intArrayOf(65536, 65536, 65536), intArrayOf(65536, 65536, 65536)),
            arrayOf(intArrayOf(65536, 65536, 65536), intArrayOf(65536, 65536, 65536)),
            arrayOf(intArrayOf(65536, 65536, 65536), intArrayOf(65536, 65536, 65536)),
            arrayOf(intArrayOf(65536, 65536, 65536), intArrayOf(65536, 65536, 65536))
        )
         var step_max = arrayOf(
            intArrayOf(45, 45, 20, 67, 101, 25),
            intArrayOf(48, 48, 14, 66, 99, 28),
            intArrayOf(48, 48, 17, 67, 100, 28)
        )

         var step_cum_grad = intArrayOf(0, 47, 93, 111, 178, 278, 305)
         var step_cum_pulse = intArrayOf(0, 46, 107, 204, 297, 325, 340, 359, 382)
         var mid_mmhg = doubleArrayOf(30.0, 40.0, 50.0) // [intensity]

         var mode_map = intArrayOf(0, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7)
         var t_ = 0
         var st_ = 0
        private var pre_battery = -1
        private var  battery_i = 0
        private var  battery_arr = IntArray(60)


    fun sensor2mmhgIdeal2(
        intensity: Int,
        mode: Int,
        sensorIndex: Int,
        pressure: Int,
        modeStepNew : Int,
        stepTime : Int
    ): Double {
        val min_val: Double = min_mmhg[intensity][mode][sensorIndex] + randomDouble(-1.0, 1.0)
        val max_val: Double = max_mmhg[intensity][mode][sensorIndex] + randomDouble(-1.0, 1.0)
        if (mode == GRADUATED_MODE) {
            val t = step_cum_grad[modeStepNew] + stepTime
            if (modeStepNew == 0) {
                return linear(max_val, min_val, t, 61)
            }
            if (sensorIndex == 0) {
                return if (t <= 159) {
                    min_val
                } else if (t > 268) {
                    max_val
                } else {
                    linear(min_val, max_val, t - 159, 268 - 159)
                }
            } else if (sensorIndex == 1) {
                return if (t <= 110) {
                    min_val
                } else if (t > 220) {
                    max_val
                } else {
                    linear(min_val, max_val, t - 110, 220 - 110)
                }
            } else if (sensorIndex == 2) {
                return if (t <= 61) {
                    min_val
                } else if (t > 183) {
                    max_val
                } else {
                    linear(min_val, max_val, t - 61, 183 - 61)
                }
            }
        } else if (mode == PULSATED_MODE) {
            var modeStep = modeStepNew
            modeStep = mode_map[modeStep]
            if (st_ != modeStep) {
                if (modeStep == 0) {
                    step_cum_pulse[8] = step_cum_pulse[7] + t_
                } else {
                    step_cum_pulse[modeStep] = step_cum_pulse[modeStep - 1] + t_
                }
                t_ = 0
            }
            st_ = modeStep
            if (stepTime > t_) {
                t_ = stepTime
            }
            val t = step_cum_pulse[modeStep] + stepTime
            val mid_val: Double = ((if (sensorIndex == 0) mid_mmhg[intensity] + randomDouble(
                -1.0,
                1.0
            ) else max_mmhg[intensity][mode][sensorIndex - 1])
                    + randomDouble(-1.0, 1.0))
            if (modeStep == 0) {
                return linear(max_mmhg[intensity][mode][0], min_val, t, step_cum_pulse[1])
            }
            if (sensorIndex == 0) {
                return if (modeStep <= 2) {
                    min_val
                } else if (modeStep == 3) {
                    linear(
                        min_val,
                        max_val,
                        t - step_cum_pulse[3],
                        step_cum_pulse[4] - step_cum_pulse[3]
                    )
                } else if (modeStep == 4) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[4],
                        step_cum_pulse[5] - step_cum_pulse[4]
                    )
                } else if (modeStep == 5) {
                    linear(
                        mid_val,
                        max_val,
                        t - step_cum_pulse[5],
                        step_cum_pulse[6] - step_cum_pulse[5]
                    )
                } else if (modeStep == 6) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[6],
                        step_cum_pulse[7] - step_cum_pulse[6]
                    )
                } else {  // if (t <= 610)
                    linear(
                        mid_val,
                        max_val,
                        t - step_cum_pulse[7],
                        step_cum_pulse[8] - step_cum_pulse[7]
                    )
                }
            } else if (sensorIndex == 1) {
                return if (modeStep == 1) {
                    min_val
                } else if (modeStep == 2) {
                    linear(
                        min_val,
                        max_val,
                        t - step_cum_pulse[2],
                        step_cum_pulse[3] - step_cum_pulse[2]
                    )
                } else if (modeStep == 3) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[3],
                        step_cum_pulse[4] - step_cum_pulse[3]
                    )
                } else if (modeStep == 4) {
                    linear(
                        mid_val,
                        max_val,
                        t - step_cum_pulse[4],
                        step_cum_pulse[5] - step_cum_pulse[4]
                    )
                } else if (modeStep == 5) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[5],
                        step_cum_pulse[6] - step_cum_pulse[5]
                    )
                } else if (modeStep == 6) {
                    linear(
                        mid_val,
                        max_val,
                        t - step_cum_pulse[6],
                        step_cum_pulse[7] - step_cum_pulse[6]
                    )
                } else {  // if (t <= 610)
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[7],
                        step_cum_pulse[8] - step_cum_pulse[7]
                    )
                }
            } else if (sensorIndex == 2) {
                return if (modeStep == 1) {
                    linear(
                        min_val,
                        max_val,
                        t - step_cum_pulse[1],
                        step_cum_pulse[2] - step_cum_pulse[1]
                    )
                } else if (modeStep == 2) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[2],
                        step_cum_pulse[3] - step_cum_pulse[2]
                    )
                } else if (modeStep == 3) {
                    linear(
                        mid_val,
                        max_val,
                        t - step_cum_pulse[3],
                        step_cum_pulse[4] - step_cum_pulse[3]
                    )
                } else if (modeStep == 4) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[4],
                        step_cum_pulse[5] - step_cum_pulse[4]
                    )
                } else if (modeStep == 5) {
                    linear(
                        mid_val,
                        max_val,
                        t - step_cum_pulse[5],
                        step_cum_pulse[6] - step_cum_pulse[5]
                    )
                } else if (modeStep == 6) {
                    linear(
                        max_val,
                        mid_val,
                        t - step_cum_pulse[6],
                        step_cum_pulse[7] - step_cum_pulse[6]
                    )
                } else {  // if (t <= 610)
                    linear(
                        mid_val,
                        max_mmhg[intensity][mode][1],
                        t - step_cum_pulse[7],
                        step_cum_pulse[8] - step_cum_pulse[7]
                    )
                }
            }
        }
        return 60.0
    }
    fun randomDouble(min_val: Double, max_val: Double): Double {
        val rand = randomGenerator.nextDouble()
        return rand * (max_val - min_val) + min_val
    }

    fun getAlpha(mmhg: Double): Float {
        val alpha = (mmhg - min_val) / (max_val - min_val) * 0.9 + 0.1
        if (alpha < 0.1) {
            return 0.1f
        }
        return if (alpha > 1.0) {
            1.0f
        } else alpha.toFloat()
    }

    fun filterBatteryValue(battery: Int): Int {
        if (battery <= 1) {
            battery_i = 0
            pre_battery = -1
            return -1
        }
        if (pre_battery == -1) {
            Arrays.fill(battery_arr, battery)
            pre_battery = battery
            return battery
        }
        battery_arr[battery_i] = battery
        battery_i = (battery_i + 1) % battery_arr.size
        val bat_val = Math.round(findAvg(battery_arr)).toInt()
        if (bat_val > pre_battery && bat_val < pre_battery + 5) {
            return pre_battery
        }
        pre_battery = bat_val
        return bat_val
    }

    private fun findAvg(arr: IntArray): Float {
        var sum = 0
        for (i in arr) {
            sum += i
        }
        return 1.0f * sum / arr.size
    }


    private fun linear(min_val: Double, max_val: Double, t: Int, max_t: Int): Double {
        val `val` = (max_val - min_val) * t / max_t + min_val
        if (max_val < min_val) {
            if (`val` > min_val) {
                return min_val
            }
            return if (`val` < max_val) {
                max_val
            } else `val`
        }
        if (`val` < min_val) {
            return min_val
        }
        return if (`val` > max_val) {
            max_val
        } else `val`
    }


    fun sensor2mmhg(
        intensity: Int,
        mode: Int,
        sensorIndex: Int,
        pressure: Int
    ): Double {
        if (pressure < 0) {
            return 60.0 //Remove glitches, x gets -127 sometimes.
        }
        if (pressure < min_x[intensity][mode][sensorIndex]) {
            min_x[intensity][mode][sensorIndex] = pressure
        } else if (pressure > max_x[intensity][mode][sensorIndex]) {
            max_x[intensity][mode][sensorIndex] = pressure
        } else {
//            return pressure * m[intensity][mode][sensorIndex] + c[intensity][mode][sensorIndex] + Algo.randomDouble(
//                -1.0,
//                1.0
//            )
        }
        val denominator = min_x[intensity][mode][sensorIndex] - max_x[intensity][mode][sensorIndex]
        if (denominator == 0) {
            return 60.0
        }

        m[intensity][mode][sensorIndex] =
            (min_mmhg[intensity][mode][sensorIndex] - max_mmhg[intensity][mode][sensorIndex]) / denominator
        c[intensity][mode][sensorIndex] =
            min_mmhg[intensity][mode][sensorIndex] - m[intensity][mode][sensorIndex] * min_x[intensity][mode][sensorIndex]
        return sensor2mmhg(intensity, mode, sensorIndex, pressure)
    }

    fun formatPressure(pressure: Double): String? {
        return sensorDF.format(pressure)
    }

    }


}