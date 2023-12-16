fun main() {

    val cachedResult = mutableMapOf<String, Int>()

    fun hashCode(input: String): Int {
        if (cachedResult.containsKey(input)) return cachedResult[input]!!

        val fold = input.fold(0) { acc, c ->
            (acc + c.code) * 17 % 256
        }

        cachedResult[input] = fold

        return fold
    }

    data class Lens(val label: String, var power: Int) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Lens) return false

            if (this.label != other.label) return false

            return true
        }

        override fun hashCode(): Int {
            return hashCode(this.label)
        }
    }

    fun parseInput(input: List<String>): List<String> {
        return input.fold("") { acc, s -> acc + s }
            .split(',')
    }

    fun parseInputToLenses(input: List<String>) = parseInput(input).map { string ->
        if (string.contains('=')) {
            val (label, power) = string.split('=')
            Lens(label, power.toInt())
        } else {
            Lens(string.dropLast(1), -1)
        }
    }.toList()

    fun part1(input: List<String>): Int {
        cachedResult.clear()
        return parseInput(input).sumOf { hashCode(it) }
    }

    fun part2(input: List<String>): Int {
        cachedResult.clear()
        val lensList = parseInputToLenses(input)
        val boxes = lensList.fold(Array(256) { mutableListOf<Lens>() }) { acc, lens ->
            when (lens.power) {
                -1 -> acc[lens.hashCode()].remove(lens)
                else -> {
                    if (acc[lens.hashCode()].contains(lens).not()) {
                        acc[lens.hashCode()].add(lens)
                    } else {
                        acc[lens.hashCode()].first { it == lens }.power = lens.power
                    }
                }
            }
            acc
        }

        return boxes.mapIndexed { index, lenses ->
            val boxNumber = (index + 1)
            lenses.mapIndexed { i, lens -> boxNumber * (i + 1) * lens.power }.sum()
        }.sum()
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}