fun main() {

    data class Brick(
        val id: Int,
        val x: Pair<Int, Int>,
        val y: Pair<Int, Int>,
        var z: Pair<Int, Int>,
        val supports: MutableSet<Int> = mutableSetOf(),
        val supportedBy: MutableSet<Int> = mutableSetOf()
    )

    fun parseInput(input: List<String>): List<Brick> {
        return input.mapIndexed { index, line ->
            val (start, end) = line.split('~')
            val (x1, y1, z1) = start.split(',').map { it.toInt() }
            val (x2, y2, z2) = end.split(',').map { it.toInt() }
            Brick(index + 1, x1 to x2, y1 to y2, z1 to z2)
        }
    }

    fun coordinatesXYMatch(one: Brick, another: Brick): Boolean {
        return ((one.x.first..one.x.second).any { it in another.x.first..another.x.second }) &&
                ((one.y.first..one.y.second).any { it in another.y.first..another.y.second })
    }

    fun landBricks(bricks: List<Brick>) {
        var change = true
        while (change) {
            change = false
            bricks.filter { it.z.first != 1 }
                .forEach { brick ->
                    val bricksBelow = bricks.filter {
                        it.z.second == brick.z.first - 1 && coordinatesXYMatch(it, brick)
                    }
                    if (bricksBelow.isEmpty()) {
                        brick.z = brick.z.first - 1 to brick.z.second - 1
                        change = true
                    }
                }
        }
    }

    fun markSupported(bricks: List<Brick>) {
        val max = bricks.map { it.z.first }.max()

        for (z in 1..max) {
            val currentBricks = bricks.filter { it.z.first == z }
            currentBricks.forEach { current ->
                val matchingAbove =
                    bricks.filter { it.z.first == current.z.second + 1 && coordinatesXYMatch(current, it) }
                val matchingBelow =
                    bricks.filter { it.z.second == current.z.first - 1 && coordinatesXYMatch(current, it) }
                current.supportedBy.addAll(matchingBelow.map { it.id })
                current.supports.addAll(matchingAbove.map { it.id })
            }
        }
    }

    fun part1(input: List<String>): Int {
        val bricks = parseInput(input).sortedBy { it.z.first }
        landBricks(bricks)
        markSupported(bricks)

        val safeToRemove = buildSet {
            bricks.forEach { brick ->
                if (brick.supports.isEmpty()) add(brick.id)
                else {
                    val supportedBricks = brick.supports.map { id -> bricks.find { it.id == id }!! }
                    if (supportedBricks.all { it.supportedBy.size > 1 }) add(brick.id)
                }
            }
        }

        return safeToRemove.size
    }

    fun part2(input: List<String>): Int {
        val bricks = parseInput(input).sortedBy { it.z.first }
        landBricks(bricks)
        markSupported(bricks)

        return bricks.sumOf { brick ->
            val fallen = mutableSetOf<Int>()
            var queue = mutableSetOf<Int>()
            queue.addAll(brick.supports)
            fallen.add(brick.id)
            while (queue.isNotEmpty()) {
                val temp = mutableSetOf<Int>()
                val bricksAbove = queue.map { id -> bricks.find { it.id == id }!! }
                bricksAbove.forEach {
                    if (it.supportedBy.all { fallen.contains(it) }) {
                        fallen.add(it.id)
                        temp.addAll(it.supports)
                    }
                }
                queue = temp
            }
            fallen.size - 1
        }
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}