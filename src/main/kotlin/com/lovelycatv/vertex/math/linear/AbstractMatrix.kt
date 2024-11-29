package com.lovelycatv.vertex.math.linear

/**
 * @author lovelycat
 * @since 2024-11-29 20:54
 * @version 1.0
 */
abstract class AbstractMatrix<E, R>(
    private val rowCreator: RowCreator<E, R>
) {
    private val matrix: MutableList<R> = mutableListOf()

    val rows: List<R> get() = this.matrix

    val cols: List<R> get() = (0..<this.getColumnCount()).map { getColumn(it) }

    constructor(rowCreator: RowCreator<E, R>, rows: Int, cols: Int) : this(rowCreator) {
        (0..<rows).forEach { _ ->
            matrix.add(rowCreator.fromSize(cols))
        }
    }

    constructor(rowCreator: RowCreator<E, R>, elements: Array<out E>) : this(rowCreator) {
        matrix[0] = rowCreator.fromElements(elements)
    }

    constructor(rowCreator: RowCreator<E, R>, rows: Collection<R>) : this(rowCreator) {
        require(rows.map { it.getSize() }.toSet().size == 1)
        matrix.addAll(rows)
    }

    fun getRowCount(): Int = matrix.size

    abstract fun getColumnCount(): Int

    fun getSize(): Pair<Int, Int> = this.matrix.size to this.getColumnCount()

    abstract fun getColumn(n: Int): R

    operator fun get(index: Int): R {
        return this.matrix[index]
    }

    fun det(): E {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()

        require(rows == cols)

        return detImplementation(rows, cols)
    }
    abstract fun detImplementation(rowCount: Int, colCount: Int): E

    fun minor(i: Int, j: Int): AbstractMatrix<E, R> {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()

        require(rows >= 2 && cols >= 2)

        return minorImplementation(rows, cols, i, j)
    }

    abstract fun minorImplementation(rowCount: Int, colCount: Int, i: Int, j: Int): AbstractMatrix<E, R>

    fun transpose(): AbstractMatrix<E, R> {
        val rowCount = this.getRowCount()
        val colCount = this.getColumnCount()
        val newMatrix = createInstance(colCount, rowCount)
        for (i in 0..<colCount) {
            for (j in 0..<rowCount) {
                newMatrix[i][j] = this[j][i]
            }
        }
        return newMatrix
    }

    fun deleteRow(row: Int): AbstractMatrix<E, R> {
        val rows = this.getRowCount()
        require(row in 0..<rows)
        return createInstance(this.matrix.filterIndexed { i, _ -> i != row }.map { it })
    }

    fun deleteColumn(column: Int): AbstractMatrix<E, R> {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()
        require(column in 0..<cols)

        val newDoubleMatrix = createInstance(rows, cols - 1)

        var tOffsetJ = 0
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                if (j == column) {
                    tOffsetJ = -1
                } else {

                    newDoubleMatrix[i][j + tOffsetJ] = this[i][j]
                }
            }
            tOffsetJ = 0
        }

        return newDoubleMatrix
    }

    abstract fun createInstance(rows: Int, cols: Int): AbstractMatrix<E, R>

    abstract fun createInstance(rows: Collection<R>): AbstractMatrix<E, R>

    abstract operator fun R.get(index: Int): E

    abstract operator fun R.set(index: Int, newValue: E)

    abstract fun R.getSize(): Int

    abstract fun R.joinToString(prefix: CharSequence, postfix: CharSequence, separator: CharSequence, limit: Int): CharSequence

    override fun toString(): String {
        return this.matrix.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ",\n ",
            limit = 10
        ) { row ->
            row.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ", ",
                limit = 10
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()

        var flag = true

        if (other is DoubleMatrix) {
            if (rows != other.getRowCount() || cols != other.getColumnCount()) {
                return false
            }
            for (i in 0..<rows) {
                for (j in 0..<cols) {
                    flag = flag && this[i][j] == other[i][j]
                }
            }
            return flag
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return matrix.hashCode()
    }

    operator fun plus(other: AbstractMatrix<E, R>): AbstractMatrix<E, R> {
        require(isTheSameShape(this, other))
        val (rows, cols) = this.getSize()
        val t = createInstance(rows, cols)
        this.plusImplementations(other, rows, cols, t)
        return t
    }

    abstract fun plusImplementations(other: AbstractMatrix<E, R>, rows: Int, cols: Int, newMatrix: AbstractMatrix<E, R>)

    operator fun unaryPlus(): AbstractMatrix<E, R> {
        val (rows, cols) = this.getSize()
        val t = createInstance(rows, cols)
        this.unaryPlusImplementation(rows, cols, t)
        return t
    }

    abstract fun unaryPlusImplementation(rows: Int, cols: Int, newMatrix: AbstractMatrix<E, R>)

    operator fun plusAssign(other: AbstractMatrix<E, R>) {
        require(isTheSameShape(this, other))
        val (rows, cols) = this.getSize()
        this.plusAssignImplementation(other, rows, cols)
    }

    abstract fun plusAssignImplementation(other: AbstractMatrix<E, R>, rows: Int, cols: Int)

    operator fun minus(other: AbstractMatrix<E, R>): AbstractMatrix<E, R> {
        require(isTheSameShape(this, other))
        val (rows, cols) = this.getSize()
        val t = createInstance(rows, cols)
        this.minusImplementations(other, rows, cols, t)
        return t
    }

    abstract fun minusImplementations(other: AbstractMatrix<E, R>, rows: Int, cols: Int, newMatrix: AbstractMatrix<E, R>)

    operator fun unaryMinus(): AbstractMatrix<E, R> {
        val (rows, cols) = this.getSize()
        val t = createInstance(rows, cols)
        this.unaryMinusImplementation(rows, cols, t)
        return t
    }

    abstract fun unaryMinusImplementation(rows: Int, cols: Int, newMatrix: AbstractMatrix<E, R>)

    operator fun minusAssign(other: AbstractMatrix<E, R>) {
        require(isTheSameShape(this, other))
        val (rows, cols) = this.getSize()
        this.minusAssignImplementation(other, rows, cols)
    }

    abstract fun minusAssignImplementation(other: AbstractMatrix<E, R>, rows: Int, cols: Int)

    operator fun times(other: AbstractMatrix<E, R>): AbstractMatrix<E, R> {
        require(couldMultiply(this, other))

        val newRows = this.getRowCount()
        val newColumns = other.getColumnCount()

        val t = createInstance(newRows, newColumns)
        this.timesImplementation(other, newRows, newColumns, t)
        return t
    }

    abstract fun timesImplementation(other: AbstractMatrix<E, R>, newRows: Int, newColumns: Int, newMatrix: AbstractMatrix<E, R>)

    operator fun timesAssign(other: AbstractMatrix<E, R>) {
        val t = times(other).matrix
        this.matrix.clear()
        this.matrix.addAll(t)
    }

    interface RowCreator<E, R> {
        fun fromSize(size: Int): R

        fun fromElements(elements: Array<out E>): R

        companion object {
            val DOUBLE_ROW_CREATOR = object : RowCreator<Double, DoubleArray> {
                override fun fromSize(size: Int): DoubleArray {
                    return DoubleArray(size)
                }

                override fun fromElements(elements: Array<out Double>): DoubleArray {
                    return DoubleArray(elements.size) { elements[it] }
                }
            }

            val FLOAT_ROW_CREATOR = object : RowCreator<Float, FloatArray> {
                override fun fromSize(size: Int): FloatArray {
                    return FloatArray(size)
                }

                override fun fromElements(elements: Array<out Float>): FloatArray {
                    return FloatArray(elements.size) { elements[it] }
                }
            }

            val INT_ROW_CREATOR = object : RowCreator<Int, IntArray> {
                override fun fromSize(size: Int): IntArray {
                    return IntArray(size)
                }

                override fun fromElements(elements: Array<out Int>): IntArray {
                    return IntArray(elements.size) { elements[it] }
                }
            }
        }
    }

    companion object {
        fun isTheSameShape(a: AbstractMatrix<*, *>, b: AbstractMatrix<*, *>): Boolean {
            return a.getSize() == b.getSize()
        }

        fun couldMultiply(a: AbstractMatrix<*, *>, b: AbstractMatrix<*, *>): Boolean {
            return a.getColumnCount() == b.getRowCount()
        }
    }
}