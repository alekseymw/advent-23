fun main() {

    fun parseInput(input: List<String>) = input.map { string ->
        string.split(" ")
            .map { it.toInt() }
    }.toList()

    fun calculateNextDifferences(differences: List<Int>) = (1..<differences.size)
        .map { differences[it] - differences[it - 1] }
        .toList()

    fun calculateNext(differences: List<Int>): Int {
        return if (differences.all { it == 0 }) 0
        else {
            val nextDifferences = calculateNextDifferences(differences)
            differences[differences.size - 1] + calculateNext(nextDifferences)
        }
    }

    fun calculateFirst(differences: List<Int>): Int {
        return if (differences.all { it == 0 }) 0
        else {
            val nextDifferences = calculateNextDifferences(differences)
            differences[0] - calculateFirst(nextDifferences)
        }
    }

    fun part1(input: List<String>): Int {
        return parseInput(input).sumOf { calculateNext(it) }
    }

    fun part2(input: List<String>): Int {
        return parseInput(input).sumOf { calculateFirst(it) }
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}