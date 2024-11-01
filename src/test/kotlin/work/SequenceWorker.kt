package work

import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWorker
import com.lovelycatv.vertex.work.data.WorkData
import kotlinx.coroutines.delay

/**
 * @author lovelycat
 * @since 2024-11-01 13:48
 * @version 1.0
 */
class SequenceWorker(workName: String, inputData: WorkData) : AbstractWorker(workName, inputData) {
    override suspend fun doWork(inputData: WorkData): WorkResult {
        println("$workName received params: ${inputData.toPairList()}")
        runInProtected {
            for (i in 0..2) {
                delay(500)
                println("Sequence Work: $workName -> $i")
            }
        }
        return WorkResult.completed(inputData + WorkData.build(workName to (500..1000).random()))
    }
}