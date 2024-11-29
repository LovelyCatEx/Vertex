package com.lovelycatv.vertex.math.linear

/**
 * @author lovelycat
 * @since 2024-10-23 23:50
 * @version 1.0
 */
class DoubleMatrix : AbstractMatrix<Double, DoubleArray> {
    constructor(rows: Int, cols: Int) : super(RowCreator.DOUBLE_ROW_CREATOR, rows, cols)

    constructor(vararg elements: Double) : super(RowCreator.DOUBLE_ROW_CREATOR, elements.toTypedArray())

    constructor(vararg rows: DoubleArray) : super(RowCreator.DOUBLE_ROW_CREATOR, rows.toList())

    constructor(rows: Collection<DoubleArray>) : super(RowCreator.DOUBLE_ROW_CREATOR, rows)

    override fun getColumnCount(): Int {
        return this[0].size
    }

    override fun DoubleArray.getSize(): Int {
        return this.size
    }

    override fun getColumn(n: Int): DoubleArray {
        return DoubleArray(this.getRowCount()) {
            this[it][n]
        }
    }

    override fun DoubleArray.get(index: Int): Double {
        return this[index]
    }

    override fun detImplementation(rowCount: Int, colCount: Int): Double {
        if (rowCount == 2) {
            return this[0][0] * this[1][1] - this[0][1] * this[1][0]
        }

        var sum = 0.0
        for (i in 0..<this.getRowCount()) {
            val flag = if ((i + 2) % 2 == 0) 1.0 else -1.0
            sum += flag * this[i][0] * this.minor(i, 0).det()
        }
        return sum
    }

    override fun minorImplementation(rowCount: Int, colCount: Int, i: Int, j: Int): DoubleMatrix {
        if (rowCount == 2) {
            return DoubleMatrix(this[1 - i][1 - j])
        }

        return deleteRow(i).deleteColumn(j) as DoubleMatrix
    }

    override fun DoubleArray.joinToString(
        prefix: CharSequence,
        postfix: CharSequence,
        separator: CharSequence,
        limit: Int
    ): CharSequence {
        return this.joinToString(separator, prefix, postfix, limit, truncated = "...")
    }

    override fun DoubleArray.set(index: Int, newValue: Double) {
        this[index] = newValue
    }

    @SuppressWarnings("UNCHECKED_CAST")
    override fun createInstance(rows: Collection<DoubleArray>): DoubleMatrix {
        return DoubleMatrix(rows)
    }

    @SuppressWarnings("UNCHECKED_CAST")
    override fun createInstance(rows: Int, cols: Int): DoubleMatrix {
        return DoubleMatrix(rows, cols)
    }

    override fun minusImplementations(
        other: AbstractMatrix<Double, DoubleArray>,
        rows: Int,
        cols: Int,
        newMatrix: AbstractMatrix<Double, DoubleArray>
    ) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] - other[i][j]
            }
        }
    }

    override fun plusImplementations(
        other: AbstractMatrix<Double, DoubleArray>,
        rows: Int,
        cols: Int,
        newMatrix: AbstractMatrix<Double, DoubleArray>
    ) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] + other[i][j]
            }
        }
    }

    override fun unaryPlusImplementation(rows: Int, cols: Int, newMatrix: AbstractMatrix<Double, DoubleArray>) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] + 1.0
            }
        }
    }

    override fun plusAssignImplementation(other: AbstractMatrix<Double, DoubleArray>, rows: Int, cols: Int) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                this[i][j] += other[i][j]
            }
        }
    }

    override fun minusAssignImplementation(other: AbstractMatrix<Double, DoubleArray>, rows: Int, cols: Int) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                this[i][j] -= other[i][j]
            }
        }
    }

    override fun unaryMinusImplementation(rows: Int, cols: Int, newMatrix: AbstractMatrix<Double, DoubleArray>) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] - 1.0
            }
        }
    }

    override fun timesImplementation(
        other: AbstractMatrix<Double, DoubleArray>,
        newRows: Int,
        newColumns: Int,
        newMatrix: AbstractMatrix<Double, DoubleArray>
    ) {
        for (i in 0..<newRows) {
            for (j in 0..<newColumns) {
                val left = this[i]
                val right = other.getColumn(j)
                newMatrix[i][j] = left dot right
            }
        }
    }
}