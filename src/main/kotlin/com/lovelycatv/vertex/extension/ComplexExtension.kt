package com.lovelycatv.vertex.extension

import com.lovelycatv.vertex.math.Complex

/**
 * @author lovelycat
 * @since 2024-10-24 13:52
 * @version 1.0
 */
fun Double.complex(j: Double = 0.0): Complex = Complex(this, j)