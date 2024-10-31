import com.lovelycatv.vertex.work.WorkChain
import com.lovelycatv.vertex.work.WorkData
import com.lovelycatv.vertex.work.WorkManager
import com.lovelycatv.vertex.work.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWork
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

            val work1 = SimpleWork("work1")
            val work2 = SimpleWork("work2")
            val work3 = SimpleWork("work3")

            val workA = ProtectedWork("workA")
            val workB = ProtectedWork("workB")

            val chainA = WorkChain.Builder()
                .parallel(work1, work2, work3)
                .sequence(workA, workB)
                .build()

            val (chain, works) = workManager.runWorkChain(chainA)
            delay(2500)

            workManager.stopWorkChain(chain, works, "REASON")

            delay(20000)
        }
    }

    class SimpleWork(workName: String) : AbstractWork(workName) {
        override suspend fun doWork(inputData: WorkData): WorkResult {
            delay((500L..2000L).random())
            println(workName)
            return WorkResult.completed()
        }
    }

    class ProtectedWork(workName: String) : AbstractWork(workName) {
        override suspend fun doWork(inputData: WorkData): WorkResult {
            runInProtected {
                for (i in (0..5)) {
                    delay(1000)
                    println("$workName: $i")
                }
            }
            return WorkResult.completed()
        }
    }
}