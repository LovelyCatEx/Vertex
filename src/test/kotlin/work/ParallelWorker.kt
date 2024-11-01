package work

import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWorker
import com.lovelycatv.vertex.work.data.WorkData

/**
 * @author lovelycat
 * @since 2024-11-01 13:48
 * @version 1.0
 */
class ParallelWorker(workName: String, inputData: WorkData) : AbstractWorker(workName, inputData) {
    override suspend fun doWork(inputData: WorkData): WorkResult {
        println("Parallel Work: $workName")
        return WorkResult.completed()
    }
}