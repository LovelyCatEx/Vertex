import com.lovelycatv.vertex.math.linear.filledDoubleMatrix
import com.lovelycatv.vertex.math.linear.identityDoubleMatrix
import com.lovelycatv.vertex.math.linear.toMatrix
import com.lovelycatv.vertex.math.linear.zeroDoubleMatrix
import com.lovelycatv.vertex.math.linear.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * @author lovelycat
 * @since 2024-10-24 21:44
 * @version 1.0
 */
class MatrixTest {
    @Test
    fun matrixCreation() {
        val fromDoubleArray = DoubleArray(4) { 1.0 }.toMatrix()
        println("1. From DoubleArray")
        println(fromDoubleArray)
        assertTrue("Matrix creation from DoubleArray is incorrect") {
            fromDoubleArray.getRowCount() == 1 && fromDoubleArray.getColumnCount() == 4 && fromDoubleArray[0].sum() == 4.0
        }

        val zeroMatrix = zeroDoubleMatrix(4)
        println("2. Zero Matrix")
        println(zeroMatrix)

        assertTrue("Matrix creation from zeroMatrix() is incorrect") {
            var flag = true
            (0..<4).forEach { rowIndex ->
                val row = zeroMatrix[rowIndex]
                flag = flag && row.toSet().size == 1 && row.toSet().iterator().next() == 0.0
            }
            flag
        }

        val identityMatrix = identityDoubleMatrix(4)
        println("3. Identity Matrix")
        println(identityMatrix)

        assertTrue("Matrix creation from identityMatrix() is incorrect") {
            var flag = true
            (0..<4).forEach { rowIndex ->
                val row = identityMatrix[rowIndex]
                flag = flag && row.sumOf { it } == 1.0
            }
            flag
        }
    }

    @Test
    fun basicMatrixOperations() {
        val a = DoubleMatrix(
            doubleArrayOf(1.0, 4.5, 3.9, 3.0),
            doubleArrayOf(2.1, 2.8, 8.1, 2.5),
            doubleArrayOf(2.0, 5.0, 6.0, 1.0)
        )
        println(a)

        println("1. Delete Row 2")
        println(a.deleteRow(1))
        assertTrue("Calculation of deletion of row is incorrect") {
            a.deleteRow(1) == DoubleMatrix(
                doubleArrayOf(1.0, 4.5, 3.9, 3.0),
                doubleArrayOf(2.0, 5.0, 6.0, 1.0)
            )
        }

        println("2. Delete Column 3")
        println(a.deleteColumn(2))
        assertTrue("Calculation of deletion of column is incorrect") {
            a.deleteColumn(2) == DoubleMatrix(
                doubleArrayOf(1.0, 4.5, 3.0),
                doubleArrayOf(2.1, 2.8, 2.5),
                doubleArrayOf(2.0, 5.0, 1.0)
            )
        }

        println("3. Transpose")
        println(a.transpose())
        assertTrue("Transpose of matrix is incorrect") {
            a.transpose() == DoubleMatrix(
                doubleArrayOf(1.0, 2.1, 2.0),
                doubleArrayOf(4.5, 2.8, 5.0),
                doubleArrayOf(3.9, 8.1, 6.0),
                doubleArrayOf(3.0, 2.5, 1.0)
            )
        }
    }

    @Test
    fun basicCalculation() {
        val a = DoubleMatrix(
            doubleArrayOf(1.0, 4.0),
            doubleArrayOf(2.0, 3.0)
        )

        val b = DoubleMatrix(
            doubleArrayOf(4.0, 1.0),
            doubleArrayOf(3.0, 2.0)
        )

        println("Matrix A:")
        println(a)
        println("Matrix B:")
        println(b)

        println("1. a + b / a += b")
        val aPlusB = a + b
        println(aPlusB)
        assertTrue("a + b is incorrect") {
            filledDoubleMatrix(2, 2,5.0) == aPlusB
        }

        a += b
        println(a)
        assertTrue("a += b is incorrect") {
            filledDoubleMatrix(2, 2,5.0) == aPlusB
        }


        println("2. a -= b / a - b")

        a -= b
        println(a)
        assertTrue("a -= b is incorrect") {
            a == DoubleMatrix(
                doubleArrayOf(1.0, 4.0),
            doubleArrayOf(2.0, 3.0)
            )
        }

        val aMinB = a - b
        println(aMinB)
        assertTrue("a - b is incorrect") {
            DoubleMatrix(
                doubleArrayOf(-3.0, 3.0),
                doubleArrayOf(-1.0, 1.0)
            ) == aMinB
        }

        println("3. a * b / a *= b")
        val aTimesB = a * b
        println(aTimesB)
        assertTrue("a * b is incorrect") {
            DoubleMatrix(
                doubleArrayOf(16.0, 9.0),
                doubleArrayOf(17.0, 8.0)
            ) == aTimesB
        }

        a *= b
        println(a)
        assertTrue("a *= b is incorrect") {
            DoubleMatrix(
                doubleArrayOf(16.0, 9.0),
                doubleArrayOf(17.0, 8.0)
            ) == aTimesB
        }

        println("4. det(a), det(b)")
        val c = DoubleMatrix(
            doubleArrayOf(1.0, 2.0, 3.0),
            doubleArrayOf(2.0, 1.0, 3.0),
            doubleArrayOf(3.0, 2.0, 1.0)
        )
        println("Matrix C:")
        println(c)

        println("a: ${a.det()}")
        println("b: ${b.det()}")
        println("c: ${c.det()}")

        assertTrue("det(a)/det(b)/det(c) is incorrect") {
            a.det() == -25.0 && b.det() == 5.0 && c.det() == 12.0
        }
    }
}