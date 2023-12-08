import kotlin.streams.toList

fun main() {

    data class RouteNode(val left: String, val right: String)

    fun parseInput(input: List<String>): Pair<String, Map<String, RouteNode>> {
        val instruction = input[0]
        val routeNodes = input.drop(2).map { inputString ->
            val regex = "[A-Z0-9]+".toRegex()
            val (from, left, right) = regex.findAll(inputString, 0).map { it.value }.toList()
            from to RouteNode(left, right)
        }.toMap()
        return instruction to routeNodes
    }

    fun part1(input: List<String>): Int {
        val parsedInput = parseInput(input)

        val directions = parsedInput.first.chars().toList()
        val queue = ArrayDeque(directions)

        var steps = 0
        var currentPosition = "AAA"
        while (currentPosition != "ZZZ") {
            val direction = queue.removeFirst()
            currentPosition =
                if (direction == 'L'.code) parsedInput.second[currentPosition]?.left.toString() else parsedInput.second[currentPosition]?.right.toString()
            steps++
            if (queue.isEmpty()) queue.addAll(directions)
        }

        return steps
    }

    fun gcd(a: Long, b: Long): Long = if (a == 0L) b else gcd(b % a, a)

    fun lcm(a: Long, b: Long): Long = (a / gcd(a, b)) * b

    fun part2(input: List<String>): Long {
        val parsedInput = parseInput(input)

        val directions = parsedInput.first.chars().toList()
        val queue = ArrayDeque(directions)

        val currentPositions = parsedInput.second.keys.filter { it.endsWith("A") }
            .toMutableList()
        val steps = Array(currentPositions.size) { 0L }

        while (queue.isNotEmpty()) {
            val direction = queue.removeFirst()
            currentPositions.asSequence()
                .forEachIndexed { index, currentPosition ->
                    if (currentPosition.endsWith("Z").not()) {
                        currentPositions[index] =
                            if (direction == 'L'.code) parsedInput.second[currentPosition]?.left.toString() else parsedInput.second[currentPosition]?.right.toString()
                        steps[index]++
                    }
                }

            if (currentPositions.all { it.endsWith("Z") }) break
            if (queue.isEmpty()) queue.addAll(directions)
        }

        return steps.reduce { acc, i -> lcm(acc, i) }.toLong()
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 2)
    val testInput2 = readInput("Day08_test2")
    check(part1(testInput2) == 6)
    val testInput3 = readInput("Day08_test3")
    check(part2(testInput3) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}