package kr.co.baress.octavecheck.utility

import java.lang.Math.PI
import java.lang.Math.sin

class TransformFFT {
    private var FFTData: DoubleArray

    constructor(data: DoubleArray, size: Int) {
        FFTData = DataInternal(data, size)
    }

    fun ResultFFT(): DoubleArray = this.FFTData

    fun DoubleArray.swap(index1: Int, index2: Int) {
        val tmp = this[index1]
        this[index1] = this[index2]
        this[index2] = tmp
    }

    private fun DataInternal(data: DoubleArray, nn: Int): DoubleArray {
        var n = 0
        var m = 0
        n = nn shl 1

        var j = 1
        for (i in 1 until n step 2) {
            if (j > i) {
                data.swap(j - 1, i - 1)
                data.swap(j, i)
            }
            m = nn
            while (m >= 2 && j > m) {
                j -= m
                m shr 1
            }
            j += m
        }

        var mmax = 2
        while (n > mmax) {
            var istep = mmax shl 1
            var theta = -(2 * PI / mmax)
            var wtemp = sin(0.5 * theta)
            var wpr = -2.0 * wtemp * wtemp
            var wpi = sin(theta)
            var wr = 1.0
            var wi = 0.0

            for (m in 1 until mmax step 2) {
                for (i in m until n step istep) {
                    j = i + mmax
                    var tempr = wr * data[j - 1] - wi * data[j]
                    var tempi = wr * data[j] + wi * data[j - 1]
                    data[j - 1] = data[i - 1] - tempr
                    data[j] = data[i] - tempi
                    data[i - 1] += tempr
                    data[i] += tempi
                }
                wtemp = wr
                wr += wr * wpr - wi * wpi
                wi += wi * wpr + wtemp * wpi
            }
            mmax = istep
        }
        return data
    }
}