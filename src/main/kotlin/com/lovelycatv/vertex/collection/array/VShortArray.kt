package com.lovelycatv.vertex.collection.array

/**
 * @author lovelycat
 * @since 2024-11-28 16:28
 * @version 1.0
 */
class VShortArray(vararg initial: Short) : VArray<Short>(*initial.toTypedArray())