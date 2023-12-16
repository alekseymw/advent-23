fun main() {

    data class Position(var x: Int, var y: Int)

    fun parseInput(
        input: List<String>,
        round: MutableList<Position>,
        cube: MutableList<Position>
    ) {
        input.forEachIndexed { i, line ->
            line.forEachIndexed { j, char ->
                when (char) {
                    '#' -> {
                        cube.add(Position(j, i))
                    }

                    'O' -> {
                        round.add(Position(j, i))
                    }
                }
            }
        }
    }

    fun north(round: MutableList<Position>, cube: List<Position>, n: Int, m: Int) {
        for (i in 0..<m) {
            val cubes = cube.filter { it.x == i }.toMutableList()
            val rounds = round.filter { it.x == i }

            var top = (0..<n).first { j -> cubes.none { it.y == j } && rounds.none { it.y == j } }
            while (top < n) {
                val floor = if (cubes.any { it.y >= top }) { cubes.removeFirst().y.plus(1) } else n
                var offset = 0
                rounds.filter { roundRock -> roundRock.y in top..<floor }
                    .sortedBy { it.y }
                    .forEach {
                        it.y = top + offset
                        offset++
                    }
                top = floor
            }
        }
    }

    fun south(round: MutableList<Position>, cube: List<Position>, n: Int, m: Int) {
        for (i in 0..<m) {
            val cubes = cube.filter { it.x == i }.toMutableList()
            val rounds = round.filter { it.x == i }

            var floor = ((n - 1).downTo(0)).first { j -> cubes.none { it.y == j } && rounds.none { it.y == j } }
            while (floor > 0) {
                val top = if (cubes.any { it.y <= floor }) { cubes.removeLast().y.minus(1) } else -1
                var offset = 0
                rounds.filter { roundRock -> roundRock.y in top + 1..floor }
                    .sortedByDescending { it.y }
                    .forEach {
                        it.y = floor - offset
                        offset++
                    }
                floor = top
            }
        }
    }

    fun west(round: MutableList<Position>, cube: List<Position>, n: Int, m: Int) {
        for (i in 0..<n) {
            val cubes = cube.filter { it.y == i }.toMutableList()
            val rounds = round.filter { it.y == i }

            var left = (0..<m).first { j -> cubes.none { it.x == j } && rounds.none { it.x == j } }
            while (left < m) {
                val right = if (cubes.any { it.x >= left }) { cubes.removeFirst().x.plus(1) } else m
                var offset = 0
                rounds.filter { roundRock -> roundRock.x in left..<right }
                    .sortedBy { it.x }
                    .forEach {
                        it.x = left + offset
                        offset++
                    }
                left = right
            }
        }
    }

    fun east(round: MutableList<Position>, cube: List<Position>, n: Int, m: Int) {
        for (i in 0..<n) {
            val cubes = cube.filter { it.y == i }.toMutableList()
            val rounds = round.filter { it.y == i }

            var right = ((m - 1).downTo(0)).first { j -> cubes.none { it.x == j } && rounds.none { it.x == j } }
            while (right > 0) {
                val left = if (cubes.any { it.x <= right }) { cubes.removeLast().x.minus(1) } else -1
                var offset = 0
                rounds.filter { roundRock -> roundRock.x in left + 1..right }
                    .sortedByDescending { it.x }
                    .forEach {
                        it.x = right - offset
                        offset++
                    }
                right = left
            }
        }
    }

    fun part1(input: List<String>): Int {
        val round = mutableListOf<Position>()
        val cube = mutableListOf<Position>()

        parseInput(input, round, cube)

        north(round, cube, input.size, input[0].length)

        return round.sumOf { input.size - it.y }
    }

    fun part2(input: List<String>): Int {
        val round = mutableListOf<Position>()
        val cube = mutableListOf<Position>()

        parseInput(input, round, cube)

        val n = input.size
        val m = input[0].length

        val cachedResult = mutableMapOf<String, Int>()

        var iteration = 0
        val start: Int
        while (true) {
            north(round, cube, n, m)
            west(round, cube, n, m)
            south(round, cube, n, m)
            east(round, cube, n, m)

            val key = round.sortedWith(compareBy<Position> { it.x }.thenBy { it.y })
                .joinToString { position -> "${position.x} ${position.y}" }
            if (cachedResult.containsKey(key)) {
                start = cachedResult[key]!!
                break
            }
            cachedResult[key] = iteration++
        }

        val length = iteration - start
        val remainingIterations = (1000000000 - start) % length - 1
        repeat(remainingIterations) {
            north(round, cube, n, m)
            west(round, cube, n, m)
            south(round, cube, n, m)
            east(round, cube, n, m)
        }

        return round.sumOf { n - it.y }
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}