fun main() {

    fun getNextSteps(grid: Array<CharArray>, y: Int, x: Int): List<Direction> {
        return buildList {
            when (grid[y][x]) {
                'v' -> add(Direction.DOWN)
                '^' -> add(Direction.UP)
                '<' -> add(Direction.LEFT)
                '>' -> add(Direction.RIGHT)
                else -> {
                    addAll(Direction.entries.toTypedArray())
                }
            }
        }
    }

    fun findLongestPath(
        x: Int,
        y: Int,
        grid: Array<CharArray>,
        visited: Array<BooleanArray>,
        distance: Int,
        ignoreSlopes: Boolean = false
    ): Int {
        if ((x in grid[0].indices).not() || (y in grid.indices).not() || visited[y][x] || grid[y][x] == '#') return 0

        if (y == grid.size - 1) {
            return distance
        }

        val nextSteps = if (ignoreSlopes) Direction.entries.toTypedArray().asList() else getNextSteps(grid, y, x)

        visited[y][x] = true

        val max =
            nextSteps.map {
                findLongestPath(x + it.dx, y + it.dy, grid, visited, distance + 1, ignoreSlopes)
            }
                .max()

        visited[y][x] = false

        return max
    }

    fun part1(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }.toTypedArray()
        val x = grid[0].indexOf('.')
        val y = 0

        val visited = Array(grid.size) { BooleanArray(grid[0].size) { false } }
        return findLongestPath(x, y, grid, visited, 0)
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }.toTypedArray()
        val x = grid[0].indexOf('.')
        val y = 0
        val visited = Array(grid.size) { BooleanArray(grid[0].size) { false } }
        return findLongestPath(x, y, grid, visited, 0, true)
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}