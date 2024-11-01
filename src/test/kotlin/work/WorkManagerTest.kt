package work

import com.lovelycatv.vertex.work.worker.WorkChain
import com.lovelycatv.vertex.work.WorkManager
import com.lovelycatv.vertex.work.data.KeyValueMergedInputDataMerger
import com.lovelycatv.vertex.work.extension.WorkerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * @author lovelycat
 * @since 2024-10-31 14:35
 */
class WorkManagerTest {
    @Test
    fun test() {
        val workManager = WorkManager()

        val p1 = WorkerBuilder<ParallelWorker>().workName("p1").build()
        val p2 = WorkerBuilder<ParallelWorker>().workName("p2").build()
        val p3 = WorkerBuilder<ParallelWorker>().workName("p3").build()

        val pi1 = WorkerBuilder<ParallelInBoundWorker>().workName("pi1").build()
        val pi2 = WorkerBuilder<ParallelInBoundWorker>().workName("pi2").build()

        val s1 = WorkerBuilder<SequenceWorker>().workName("s1").build()
        val s2 = WorkerBuilder<SequenceWorker>().workName("s2").build()
        val s3 = WorkerBuilder<SequenceWorker>().workName("s3").build()

        val chainA = WorkChain.Builder()
            .parallel(p1, p2, p3)
            .parallelInBound(pi1, pi2)
            .transmit(KeyValueMergedInputDataMerger())
            .sequence(s1, s2, s3)
            .build()

        workManager.runWorkChain(chainA)

        runBlocking {
            delay(10000)
        }
    }
}