package com.lovelycatv.vertex.work.exception

import com.lovelycatv.vertex.work.base.AbstractWork

/**
 * @author lovelycat
 * @since 2024-10-27 20:41
 * @version 1.0
 */
class WorkNotCompletedException(work: AbstractWork) : RuntimeException("Work ${work.workId} is still running")