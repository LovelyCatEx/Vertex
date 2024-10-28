package com.lovelycatv.vertex.math.linear

import com.lovelycatv.vertex.extension.dot
import com.lovelycatv.vertex.extension.emptyDoubleArray

/**
 * @author lovelycat
 * @since 2024-10-23 23:50
 * @version 1.0
 */
class Matrix {
    private val matrix: MutableList<DoubleArray> = mutableListOf()

    constructor(rows: Int, cols: Int) {
        (0..<rows).forEach { _ ->
            matrix.add(emptyDoubleArray(cols))
        }
    }

    constructor(vararg elements: Double) {
        matrix[0] = elements
    }

    constructor(vararg rows: DoubleArray) {
        require(rows.map { it.size }.toSet().size == 1)
        matrix.addAll(rows)
    }

    constructor(rows: Iterable<DoubleArray>) {
        require(rows.map { it.size }.toSet().size == 1)
        matrix.addAll(rows)
    }

    fun getRowCount(): Int = matrix.size

    fun getColumnCount(): Int = matrix[0].size

    fun getColumn(n: Int): DoubleArray {
        require(n < getColumnCount())
        return DoubleArray(getRowCount()) {
            this[it][n]
        }
    }

    operator fun get(index: Int): DoubleArray {
        return this.matrix[index]
    }

    fun det(): Double {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()

        require(rows == cols)

        if (rows == 2) {
            return this[0][0] * this[1][1] - this[0][1] * this[1][0]
        }

        var sum = 0.0
        for (i in 0..<this.getRowCount()) {
            val flag = if ((i + 2) % 2 == 0) 1.0 else -1.0
            sum += flag * this[i][0] * this.minor(i, 0).det()
        }
        return sum
    }

    fun minor(i: Int, j: Int): Matrix {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()

        require(rows >= 2 && cols >= 2)

        if (rows == 2) {
            return Matrix(this[1 - i][1 - j])
        }

        return deleteRow(i).deleteColumn(j)
    }

    fun transpose(): Matrix {
        val newRows = this.getColumnCount()
        val newCols = this.getRowCount()
        val newMatrix = Matrix(newRows, newCols)
        for (i in 0..<newRows) {
            for (j in 0..<newCols) {
                newMatrix[i][j] = this[j][i]
            }
        }
        return newMatrix
    }

    fun deleteRow(row: Int, newInstance: Boolean = true): Matrix {
        val rows = this.getRowCount()
        require(row in 0..<rows)
        return Matrix(this.matrix.filterIndexed { i, _ -> i != row }.map { it.clone() })
    }

    fun deleteColumn(column: Int, newInstance: Boolean = true): Matrix {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()
        require(column in 0..<cols)

        val newMatrix = Matrix(rows, cols - 1)

        var tOffsetJ = 0
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                if (j == column) {
                    tOffsetJ = -1
                } else {
                    newMatrix[i][j + tOffsetJ] = this[i][j]
                }
            }
            tOffsetJ = 0
        }

        return newMatrix
    }

    fun getSize(): Pair<Int, Int> = this.matrix.size to this.matrix[0].size

    operator fun plus(other: Matrix): Matrix {
        val (rows, cols) = this.getSize()
        require(rows == other.getRowCount() && cols == other.getColumnCount())
        val newMatrix = Matrix(rows, cols)
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] + other[i][j]
            }
        }
        return newMatrix
    }

    operator fun unaryPlus(): Matrix {
        val (rows, cols) = this.getSize()
        val newMatrix = Matrix(rows, cols)
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] + 1.0
            }
        }
        return newMatrix
    }

    operator fun plusAssign(other: Matrix) {
        val (rows, cols) = this.getSize()
        require(rows == other.getRowCount() && cols == other.getColumnCount())
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                this[i][j] += other[i][j]
            }
        }
    }

    operator fun minus(other: Matrix): Matrix {
        val (rows, cols) = this.getSize()
        require(rows == other.getRowCount() && cols == other.getColumnCount())
        val newMatrix = Matrix(rows, cols)
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] - other[i][j]
            }
        }
        return newMatrix
    }

    operator fun unaryMinus(): Matrix {
        val (rows, cols) = this.getSize()
        val newMatrix = Matrix(rows, cols)
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                newMatrix[i][j] = this[i][j] - 1.0
            }
        }
        return newMatrix
    }

    operator fun minusAssign(other: Matrix) {
        val (rows, cols) = this.getSize()
        require(rows == other.getRowCount() && cols == other.getColumnCount())
        for (i in 0..<rows) {
            for (j in 0..<cols) {
                this[i][j] -= other[i][j]
            }
        }
    }

    operator fun times(other: Matrix): Matrix {
        val (rows, cols) = this.getSize()
        require(cols == other.getRowCount())

        val newRows = this.getRowCount()
        val newColumns = other.getColumnCount()

        val newMatrix = Matrix(newRows, newColumns)

        for (i in 0..<newRows) {
            for (j in 0..<newColumns) {
                val left = this[i]
                val right = other.getColumn(j)
                newMatrix[i][j] = left dot right
            }
        }
        return newMatrix
    }

    operator fun timesAssign(other: Matrix) {
        val t = times(other).matrix
        this.matrix.clear()
        this.matrix.addAll(t)
    }

    override fun toString(): String {
        var innerStr = ""
        this.matrix.forEach { row ->
            innerStr += "["
            row.forEach { elementInRow ->
                innerStr += "${elementInRow}, "
            }
            innerStr = innerStr.dropLast(2)
            innerStr += "],\n"
        }
        innerStr = innerStr.dropLast(2)
        return "[${innerStr}]"
    }

    override fun equals(other: Any?): Boolean {
        val rows = this.getRowCount()
        val cols = this.getColumnCount()

        var flag = true

        if (other is Matrix) {
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

}