package com.lovelycatv.vertex.math.linear

/**
 * @author lovelycat
 * @since 2024-10-23 23:50
 * @version 1.0
 */
class FloatMatrix : AbstractMatrix<Float, FloatArray> {
    constructor(rows: Int, cols: Int) : super(RowCreator.FLOAT_ROW_CREATOR, rows, cols)

    constructor(vararg elements: Float) : super(RowCreator.FLOAT_ROW_CREATOR, elements.toTypedArray())

    constructor(vararg rows: FloatArray) : super(RowCreator.FLOAT_ROW_CREATOR, rows.toList())

    constructor(rows: Collection<FloatArray>) : super(RowCreator.FLOAT_ROW_CREATOR, rows)

    override fun getColumnCount(): Int {
        return this[0].size
    }

    override fun FloatArray.getSize(): Int {
        return this.size
    }

    override fun getColumn(n: Int): FloatArray {
        return FloatArray(this.getRowCount()) {
            this[it][n]
        }
    }

    override fun FloatArray.get(index: Int): Float {
        return this[index]
    }

    override fun detImplementation(rowCount: Int, colCount: Int): Float {
        if (rowCount == 2) {
            return this[0][0] * this[1][1] - this[0][1] * this[1][0]
        }

        var sum = 0f
        for (i in 0..<this.getRowCount()) {
            val flag = if ((i + 2) % 2 == 0) 1f else -1f
            sum += flag * this[i][0] * this.minor(i, 0).det()
        }
        return sum
    }

    override fun minorImplementation(rowCount: Int, colCount: Int, i: Int, j: Int): FloatMatrix {
        if (rowCount == 2) {
            return FloatMatrix(this[1 - i][1 - j])
        }

        return deleteRow(i).deleteColumn(j) as FloatMatrix
    }

    override fun FloatArray.joinToString(
        prefix: CharSequence,
        postfix: CharSequence,
        separator: CharSequence,
        limit: Int
    ): CharSequence {
        return this.joinToString(separator, prefix, postfix, limit, truncated = "...")
    }

    override fun FloatArray.set(index: Int, newValue: Float) {
        this[index] = newValue
    }

    @SuppressWarnings("UNCHECKED_CAST")
    override fun createInstance(rows: Collection<FloatArray>): FloatMatrix {
        return FloatMatrix(rows)
    }

    @SuppressWarnings("UNCHECKED_CAST")
    override fun createInstance(rows: Int, cols: Int): FloatMatrix {
        return FloatMatrix(rows, cols)
    }

    override fun minusImplementations(
        other: AbstractMatrix<Float, FloatArray>,
        rows: Int,
        cols: Int,
        newMatrix: AbstractMatrix<Float, FloatArray>
    ) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] - other[i][j]
            }
        }
    }

    override fun plusImplementations(
        other: AbstractMatrix<Float, FloatArray>,
        rows: Int,
        cols: Int,
        newMatrix: AbstractMatrix<Float, FloatArray>
    ) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] + other[i][j]
            }
        }
    }

    override fun unaryPlusImplementation(rows: Int, cols: Int, newMatrix: AbstractMatrix<Float, FloatArray>) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] + 1f
            }
        }
    }

    override fun plusAssignImplementation(other: AbstractMatrix<Float, FloatArray>, rows: Int, cols: Int) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                this[i][j] += other[i][j]
            }
        }
    }

    override fun minusAssignImplementation(other: AbstractMatrix<Float, FloatArray>, rows: Int, cols: Int) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                this[i][j] -= other[i][j]
            }
        }
    }

    override fun unaryMinusImplementation(rows: Int, cols: Int, newMatrix: AbstractMatrix<Float, FloatArray>) {
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] - 1f
            }
        }
    }

    override fun timesImplementation(
        other: AbstractMatrix<Float, FloatArray>,
        newRows: Int,
        newColumns: Int,
        newMatrix: AbstractMatrix<Float, FloatArray>
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