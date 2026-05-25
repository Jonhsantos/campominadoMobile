package com.aula.campominado.game

enum class CellState {
    HIDDEN, REVEALED, FLAGGED
}

data class Cell(
    var isMine: Boolean = false,
    var adjacentMines: Int = 0,
    var state: CellState = CellState.HIDDEN
)

class MinesweeperGame(
    val rows: Int = 9,
    val cols: Int = 9,
    val mineCount: Int = 10
) {
    private val grid: Array<Array<Cell>> = Array(rows) { Array(cols) { Cell() } }
    private var minesPlaced = false
    var gameOver = false
    var won = false
    var revealedSafeCells = 0

    fun reset() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                grid[r][c] = Cell()
            }
        }
        minesPlaced = false
        gameOver = false
        won = false
        revealedSafeCells = 0
    }

    fun getCell(row: Int, col: Int): Cell = grid[row][col]

    fun reveal(row: Int, col: Int): Boolean {
        if (gameOver || !isValid(row, col)) return false
        val cell = grid[row][col]
        if (cell.state == CellState.FLAGGED || cell.state == CellState.REVEALED) return false

        if (!minesPlaced) {
            placeMines(row, col)
        }

        if (cell.isMine) {
            cell.state = CellState.REVEALED
            gameOver = true
            won = false
            revealAllMines()
            return true
        }

        floodReveal(row, col)
        checkWin()
        return true
    }

    fun toggleFlag(row: Int, col: Int): Boolean {
        if (gameOver || !isValid(row, col)) return false
        val cell = grid[row][col]
        if (cell.state == CellState.REVEALED) return false
        cell.state = if (cell.state == CellState.FLAGGED) CellState.HIDDEN else CellState.FLAGGED
        return true
    }

    fun calculateScore(): Int {
        var score = revealedSafeCells * 10
        if (won) score += 500
        return score
    }

    private fun placeMines(safeRow: Int, safeCol: Int) {
        var placed = 0
        while (placed < mineCount) {
            val r = (0 until rows).random()
            val c = (0 until cols).random()
            if ((r == safeRow && c == safeCol) || grid[r][c].isMine) continue
            grid[r][c].isMine = true
            placed++
        }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!grid[r][c].isMine) {
                    grid[r][c].adjacentMines = countAdjacentMines(r, c)
                }
            }
        }
        minesPlaced = true
    }

    private fun floodReveal(row: Int, col: Int) {
        if (!isValid(row, col)) return
        val cell = grid[row][col]
        if (cell.state != CellState.HIDDEN || cell.isMine) return

        cell.state = CellState.REVEALED
        revealedSafeCells++

        if (cell.adjacentMines == 0) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr != 0 || dc != 0) {
                        floodReveal(row + dr, col + dc)
                    }
                }
            }
        }
    }

    private fun checkWin() {
        val totalSafe = rows * cols - mineCount
        if (revealedSafeCells >= totalSafe) {
            gameOver = true
            won = true
        }
    }

    private fun revealAllMines() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c].isMine) {
                    grid[r][c].state = CellState.REVEALED
                }
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (isValid(nr, nc) && grid[nr][nc].isMine) count++
            }
        }
        return count
    }

    private fun isValid(row: Int, col: Int): Boolean =
        row in 0 until rows && col in 0 until cols
}
