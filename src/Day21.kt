fun main() {

    fun isValidPoint(x: Int, y: Int, grid: Array<CharArray>): Boolean {
        return (x in grid[0].indices) && (y in grid.indices) && grid[y][x] != '#'
    }

    fun part1(input: List<String>, maxSteps: Int): Int {
        val grid = input.map { it.toCharArray() }.toTypedArray()
        var startX = 0
        var startY = 0

        run startLoop@{
            grid.forEachIndexed { i, chars ->
                val j = chars.indexOf('S')
                if (j != -1) {
                    startX = j
                    startY = i
                    return@startLoop
                }
            }
        }

        var points = mutableSetOf<Pair<Int, Int>>()
        points.add(startX to startY)
        repeat(maxSteps) {
            val temp = mutableSetOf<Pair<Int, Int>>()

            for (point in points) {
                Direction.entries.forEach {
                    val (x, y) = point.first + it.dx to point.second + it.dy
                    if (isValidPoint(x, y, grid)) temp.add(x to y)
                }
            }

            points = temp
        }

        return points.size
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)

    val input = readInput("Day21")
    part1(input, 64).println()
}