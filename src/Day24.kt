fun main() {

    data class HailStone(val id: Int, val x: Long, val y: Long, val z: Long, val dx: Long, val dy: Long, val dz: Long)

    fun parseInput(input: List<String>): List<HailStone> {
        return input.mapIndexed { index, line ->
            val (position, velocity) = line.split('@')
            val (x, y, z) = position.split(',').map { it.trim().toLong() }
            val (dx, dy, dz) = velocity.split(',').map { it.trim().toLong() }
            HailStone(index, x, y, z, dx, dy, dz)
        }
    }

    fun pointInFuture(px: Double, py: Double, hailStone: HailStone): Boolean {
        return ((px >= hailStone.x && hailStone.dx > 0) || (px <= hailStone.x && hailStone.dx < 0)) &&
                ((py >= hailStone.y && hailStone.dy > 0) || (py <= hailStone.y && hailStone.dy < 0))
    }

    fun part1(input: List<String>, min: Long, max: Long): Int {
        val hailStones = parseInput(input)
        val results = mutableMapOf<Pair<Int, Int>, Pair<Double, Double>>()
        for (i in hailStones.indices) {
            val m1 = hailStones[i].dy.toDouble() / hailStones[i].dx
            val b1 = hailStones[i].y - m1 * hailStones[i].x
            for (j in i + 1..<hailStones.size) {
                val m2 = hailStones[j].dy.toDouble() / hailStones[j].dx
                val b2 = hailStones[j].y - m2 * hailStones[j].x

                val px = (b2 - b1) / (m1 - m2)
                val py = m1 * px + b1

                if (pointInFuture(px, py, hailStones[i]) && pointInFuture(px, py, hailStones[j])) {
                    results[hailStones[i].id to hailStones[j].id] = px to py
                }
            }
        }

        return results.count { (it.value.first >= min && it.value.first <= max) && (it.value.second >= min && it.value.second <= max) }
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput, 7, 27) == 2)

    val input = readInput("Day24")
    part1(input, 200000000000000, 400000000000000).println()
}