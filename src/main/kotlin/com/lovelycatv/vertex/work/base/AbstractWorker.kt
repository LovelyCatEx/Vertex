package com.lovelycatv.vertex.work.base

import com.lovelycatv.vertex.work.WorkData

/**
 * @author lovelycat
 * @since 2024-10-31 18:54
 * @version 1.0
 */
abstract class AbstractWorker(
    workName: String,
    inputData: WorkData
) : AbstractStateWork(workName, inputData)