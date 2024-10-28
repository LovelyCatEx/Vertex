package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.map.AnyMap

/**
 * @author lovelycat
 * @since 2024-10-27 20:22
 * @version 1.0
 */
class WorkData : AnyMap<String>() {
    companion object {
        fun build(vararg pairs: Pair<String, Any?>): WorkData {
            val workData = WorkData()
            workData.putAll(pairs)
            return workData
        }
    }
}