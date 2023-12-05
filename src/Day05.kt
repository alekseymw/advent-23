import kotlin.math.abs

fun main() {

    fun getNumbers(input: String) = "\\d+".toRegex().findAll(input, 0)
        .map { it.value.toLong() }

    fun part1(input: List<String>): Long {
        val seeds = getNumbers(input[0]).toMutableList()

        val visited = MutableList(seeds.size) { false }
        for (i in 1..<input.size) {
            if (input[i].isBlank()) visited.replaceAll { false }
            else
                if (input[i].any { it.isDigit() }) {
                    val transformNumbers = getNumbers(input[i]).toList()
                    seeds.forEachIndexed { index, seed ->
                        if (visited[index].not() && seed in transformNumbers[1].rangeUntil(transformNumbers[1] + transformNumbers[2])) {
                            seeds[index] = transformNumbers[0] + (seeds[index] - transformNumbers[1])
                            visited[index] = true
                        }
                    }
                }
        }

        return seeds.min()
    }

    data class SeedRange(val numbersRange: LongRange)
    data class TransformRange(val transformFromRange: LongRange, val transformToRange: LongRange)

    fun part2(input: List<String>): Long {
        val seeds = getNumbers(input[0]).toList()
        var seedRanges = (seeds.indices step 2).map {
            SeedRange(seeds[it]..seeds[it] + seeds[it + 1])
        }.toMutableList()

        val transformMap = mutableListOf<TransformRange>()
        for (i in 2..<input.size) {
            if (input[i].any { it.isDigit() }) {
                val transformNumbers = getNumbers(input[i]).toList()
                val transformToStart = transformNumbers[0]
                val transformFromStart = transformNumbers[1]
                val length = transformNumbers[2]
                transformMap.add(
                    TransformRange(
                        transformFromStart..transformFromStart + length,
                        transformToStart..transformToStart + length
                    )
                )
            }
            if (input[i].isBlank() || i == input.size - 1) {
                val temp = mutableListOf<SeedRange>()

                seedRanges.forEach mainLoop@ { seedRange ->
                    val numbersRange = seedRange.numbersRange
                    val sizeBefore = temp.size
                    transformMap.forEach { transformRange ->
                        val transformFromRange = transformRange.transformFromRange
                        if (transformFromRange.contains(numbersRange.first) || transformFromRange.contains(numbersRange.last)
                            || (transformFromRange.first > numbersRange.first && transformFromRange.last < numbersRange.last)) {
                            val newRangeStart =
                                maxOf(numbersRange.first, transformFromRange.first)
                            val newRangeEnd =
                                minOf(numbersRange.last, transformFromRange.last)

                            val shift = abs(newRangeStart - transformFromRange.first)
                            val length = newRangeEnd - newRangeStart

                            val transformToRangeStart = transformRange.transformToRange.first + shift
                            temp.add(SeedRange(transformToRangeStart..transformToRangeStart + length))

                            if (numbersRange.first < transformFromRange.first) temp.add(SeedRange(numbersRange.first..<transformFromRange.first))
                            if (numbersRange.last > transformFromRange.last) temp.add(SeedRange(transformFromRange.last..<numbersRange.last))

                            return@mainLoop
                        }
                    }
                    if (temp.size == sizeBefore) temp.add(seedRange)
                }

                seedRanges = temp
                transformMap.clear()
            }
        }

        return seedRanges.filter { it.numbersRange.first != 0L }
            .minOf { it.numbersRange.first }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}