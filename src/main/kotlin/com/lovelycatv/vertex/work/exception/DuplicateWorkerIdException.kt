package com.lovelycatv.vertex.work.exception

/**
 * @author lovelycat
 * @since 2024-10-31 19:40
 * @version 1.0
 */
class DuplicateWorkerIdException(workerId: String) : RuntimeException("Duplicate worker id: $workerId")