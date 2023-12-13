fun main() {

    data class SpringRow(val input: String, val sizes: List<Int>)

    fun parseInput(input: List<String>): List<SpringRow> {
        return input.map {
            val (inputString, sizesString) = it.split(' ')
            inputString to sizesString
        }.map {
            val sizes = it.second.split(',').map { it.toInt() }
            SpringRow(it.first, sizes)
        }.toList()
    }

    val cachedResult = mutableMapOf<String, Long>()

    fun findArrangements(
        input: String,
        sizes: List<Int>,
        stringIndex: Int = 0,
        sizeIndex: Int = 0,
        hashNumber: Int = 0
    ): Long {
        val key = "${stringIndex}-${sizeIndex}-${hashNumber}"
        if (cachedResult.contains(key)) return cachedResult[key]!!

        if (stringIndex == input.length) {
            if (sizeIndex == sizes.size && hashNumber == 0)
                return 1
            if (sizeIndex == sizes.size - 1 && hashNumber == sizes[sizeIndex])
                return 1
            return 0
        }

        if (input[stringIndex] == '.') {
            return when (hashNumber) {
                0 -> {
                    cachedResult[key] = findArrangements(input, sizes, stringIndex + 1, sizeIndex)
                    cachedResult[key]!!
                }

                sizes.getOrNull(sizeIndex) -> {
                    cachedResult[key] = findArrangements(input, sizes, stringIndex + 1, sizeIndex + 1)
                    cachedResult[key]!!
                }

                else -> {
                    cachedResult[key] = 0
                    0
                }
            }
        } else if (input[stringIndex] == '#') {
            cachedResult[key] = findArrangements(input, sizes, stringIndex + 1, sizeIndex, hashNumber + 1)
            return cachedResult[key]!!
        } else if (input[stringIndex] == '?') {
            val dotResult = when (hashNumber) {
                0 -> {
                    findArrangements(input, sizes, stringIndex + 1, sizeIndex)
                }

                sizes.getOrNull(sizeIndex) -> {
                    findArrangements(input, sizes, stringIndex + 1, sizeIndex + 1)
                }

                else -> {
                    0
                }
            }
            val hashResult = findArrangements(input, sizes, stringIndex + 1, sizeIndex, hashNumber + 1)

            cachedResult[key] = dotResult + hashResult
            return cachedResult[key]!!
        } else
            return 0
    }

    fun part1(input: List<String>): Int {
        val parseInput = parseInput(input)
        return parseInput.sumOf {
            cachedResult.clear()
            val findArrangements = findArrangements(it.input, it.sizes)
            findArrangements
        }.toInt()
    }

    fun part2(input: List<String>): Long {
        val parseInput = parseInput(input)
        return parseInput.sumOf { springRow ->
            cachedResult.clear()
            val newInput =
                "${springRow.input}?${springRow.input}?${springRow.input}?${springRow.input}?${springRow.input}"
            val newSizes = buildList {
                (1..5).forEach { _ -> addAll(springRow.sizes) }
            }
            val findArrangements = findArrangements(newInput, newSizes)
            findArrangements
        }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}