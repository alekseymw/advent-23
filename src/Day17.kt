import java.util.*

fun main() {

    data class State(val x: Int, val y: Int, var moves: Int, var direction: Direction, var cost: Int)
    data class Key(val x: Int, val y: Int, val moves: Int, val direction: Direction)

    fun isValidPath(x: Int, y: Int, grid: Array<IntArray>): Boolean {
        return y in grid.indices && x in grid[0].indices
    }

    fun correctDirection(nextDirection: Direction, currentDirection: Direction): Boolean {
        return when (nextDirection) {
            Direction.UP -> {
                currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT || currentDirection == Direction.UP
            }

            Direction.DOWN -> {
                currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT || currentDirection == Direction.DOWN
            }

            Direction.RIGHT -> {
                currentDirection == Direction.UP || currentDirection == Direction.DOWN || currentDirection == Direction.RIGHT
            }

            Direction.LEFT -> {
                currentDirection == Direction.UP || currentDirection == Direction.DOWN || currentDirection == Direction.LEFT
            }
        }
    }

    fun parseInput(input: List<String>): Array<IntArray> {
        return input.foldIndexed(Array(input.size) { IntArray(input[0].length) }) { index, array, line ->
            array[index] = line.toCharArray().map { it.digitToInt() }.toIntArray()
            array
        }
    }

    fun calculatePath(grid: Array<IntArray>, minStep: Int = 1, maxMoves: Int = 3): Int {
        val m = grid.size
        val n = grid[0].size

        val costs = Array(m) { IntArray(n) { Int.MAX_VALUE } }

        val queue = PriorityQueue<State>(compareBy { it.cost })
        queue.offer(State(0, 0, 0, Direction.RIGHT, 0))
        val visited = mutableSetOf<Key>()

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val x = current.x
            val y = current.y

            costs[y][x] = current.cost
            if (x == n - 1 && y == m - 1)
                return current.cost

            if (visited.add(Key(current.x, current.y, current.moves, current.direction)).not()) continue

            Direction.entries.filter { correctDirection(it, current.direction) }
                .forEach {
                    val newX = x + it.dx
                    val newY = y + it.dy

                    val newMoves = if (current.direction == it) current.moves + 1 else 1
                    if (newMoves > maxMoves) return@forEach
                    if (isValidPath(newX, newY, grid)) {
                        val newState = State(newX, newY, newMoves, it, current.cost + grid[newY][newX])
                        if (current.direction == it) {
                            queue.offer(newState)
                        } else {
                            if (current.moves == 0 || current.moves >= minStep) queue.offer(newState)
                        }
                    }
                }
        }

        return -1
    }

    fun part1(input: List<String>): Int {
        val grid = parseInput(input)
        return calculatePath(grid)
    }

    fun part2(input: List<String>): Int {
        val grid = parseInput(input)
        return calculatePath(grid, 4, 10)
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}