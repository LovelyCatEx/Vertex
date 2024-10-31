import com.lovelycatv.vertex.work.WorkChain
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.WorkManager
import com.lovelycatv.vertex.work.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWorker
import com.lovelycatv.vertex.work.base.WrappedWorker
import com.lovelycatv.vertex.work.extension.WorkerBuilder
import com.lovelycatv.vertex.work.interceptor.DefaultWorkChainInterceptor
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
        runBlocking {
            val workManager = WorkManager()

            val work1 = WorkerBuilder<SimpleWork>().workName("work1").retry(3) { 1000 }.interruptBlockWhenFailure().build()
            val work2 = WorkerBuilder<SimpleWork>().workName("work2").retry(3) { 800 }.interruptBlockWhenFailure().build()
            val work3 = WorkerBuilder<SimpleWork>().workName("work3").retry(3) { 600 }.interruptBlockWhenFailure().build()

            val workA = WorkerBuilder<ProtectedWork>().workName("workA").retry(3) { 200 }.interruptChainWhenFailure().build()
            val workB = WorkerBuilder<ProtectedWork>().workName("workB").build()

            val workC = WorkerBuilder<ProtectedWork>().workName("workC").build()
            val workD = WorkerBuilder<ProtectedWork>().workName("workD").build()

            val chainA = WorkChain.Builder()
                .parallelInBound(work1, work2, work3)
                .sequence(workA, workB)
                .sequence(workC, workD)
                .build()

            val (chain, works) = workManager.runWorkChain(chainA, interceptor = object : DefaultWorkChainInterceptor() {
                override fun beforeBlockStarted(blockIndex: Int, block: WorkChain.Block) {
                    println(">>> $blockIndex started")
                }

                override fun onBlockInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWorker) {
                    println("${producer.getWorkerId()} produced the block interrupted")
                }

                override fun onChainInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWorker) {
                    println("${producer.getWorkerId()} produced the chain interrupted")
                }
            })

            delay(20000)
        }
    }

    class SimpleWork(workName: String, inputData: WorkData) : AbstractWorker(workName, inputData) {
        override suspend fun doWork(inputData: WorkData): WorkResult {
            println(workName)
            throw RuntimeException("123456")
            return WorkResult.completed()
        }
    }

    class ProtectedWork(workName: String, inputData: WorkData) : AbstractWorker(workName, inputData) {
        override suspend fun doWork(inputData: WorkData): WorkResult {
            for (i in (0..5)) {
                delay(1000)
                println("$workName: $i")
                if ((0..10).random() >= 4) {
                    throw RuntimeException("123456")
                }
            }
            return WorkResult.completed()
        }
    }

    class ProtectedWork2(workName: String, inputData: WorkData) : AbstractWorker(workName, inputData) {
        override suspend fun doWork(inputData: WorkData): WorkResult {
            for (i in (0..5)) {
                delay(1000)
                println("$workName: $i")
            }
            return WorkResult.completed()
        }
    }
}