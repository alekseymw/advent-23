import kotlin.math.abs

fun main() {

    data class Galaxy(var x: Long, var y: Long)

    fun parseInput(input: List<String>): List<Galaxy> {
        return input.foldIndexed(mutableListOf()) { y, list, line ->
            for (char in line.toCharArray().withIndex()) {
                if (char.value == '#') {
                    list.add(Galaxy(char.index.toLong(), y.toLong()))
                }
            }
            list
        }
    }

    fun findEmptyRowsAndColumns(galaxyList: List<Galaxy>, input: List<String>): Pair<List<Int>, List<Int>> {
        return input[0].indices.filter { i -> galaxyList.none { it.x == i.toLong() } }.toList() to
                input.indices.filter { i -> galaxyList.none { it.y == i.toLong() } }.toList()
    }

    fun calculate(
        galaxyList: List<Galaxy>
    ) = galaxyList.foldIndexed(mutableListOf<Long>()) { i, list, galaxy ->
        for (k in i + 1..<galaxyList.size) {
            val distance: Long = abs(galaxy.y - galaxyList[k].y) + abs(galaxy.x - galaxyList[k].x)
            list.add(distance)
        }
        list
    }
        .sum()

    fun List<Galaxy>.expand(emptyColumns: List<Int>, emptyRows: List<Int>, size: Int) {
        this.forEach { galaxy ->
            var newX = galaxy.x
            var newY = galaxy.y
            emptyColumns.forEach { column -> if (galaxy.x > column) newX += size - 1 }
            emptyRows.forEach { row -> if (galaxy.y > row) newY += size - 1 }
            galaxy.x = newX
            galaxy.y = newY
        }
    }

    fun part1(input: List<String>): Int {
        val galaxyList = parseInput(input)
        val (emptyCols, emptyRows) = findEmptyRowsAndColumns(galaxyList, input)
        galaxyList.expand(emptyCols, emptyRows, 2)
        return calculate(galaxyList).toInt()
    }

    fun part2(input: List<String>, size: Int): Long {
        val galaxyList = parseInput(input)
        val (emptyCols, emptyRows) = findEmptyRowsAndColumns(galaxyList, input)
        galaxyList.expand(emptyCols, emptyRows, size)
        return calculate(galaxyList)
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)
    check(part2(testInput, 10) == 1030L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input, 1000000).println()
}