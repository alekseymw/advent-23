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
                    val (transformTo, transformFrom, length) = getNumbers(input[i]).toList()
                    seeds.forEachIndexed { index, seed ->
                        if (visited[index].not() && seed in transformFrom.rangeUntil(transformFrom + length)) {
                            seeds[index] = transformTo + (seeds[index] - transformFrom)
                            visited[index] = true
                        }
                    }
                }
        }

        return seeds.min()
    }

    data class SeedRange(val start: Long, val end: Long)
    data class TransformRange(val fromStart: Long, val fromEnd: Long, val toStart: Long, val toEnd: Long)

    fun part2(input: List<String>): Long {
        val seeds = getNumbers(input[0]).toList()
        var seedRanges = (seeds.indices step 2).map {
            SeedRange(seeds[it], seeds[it] + seeds[it + 1])
        }.toMutableList()

        val transformMap = ArrayList<TransformRange>()
        for (i in 2..<input.size) {
            if (input[i].any { it.isDigit() }) {
                val (transformToStart, transformFromStart, length) = getNumbers(input[i]).toList()
                transformMap.add(
                    TransformRange(
                        transformFromStart, transformFromStart + length,
                        transformToStart, transformToStart + length
                    )
                )
            }
            if (input[i].isBlank() || i == input.size - 1) {
                val queue: ArrayDeque<SeedRange> = ArrayDeque(seedRanges)
                val temp = mutableListOf<SeedRange>()

                while (queue.isEmpty().not()) {
                    val seedRange = queue.removeFirst()

                    val filteredTransformMap = transformMap.asSequence()
                        .filter { transformRange ->
                            ((transformRange.fromStart <= seedRange.start && transformRange.fromEnd <= seedRange.end && transformRange.fromEnd >= seedRange.start) ||
                                    (transformRange.fromStart >= seedRange.start && transformRange.fromEnd >= seedRange.end && transformRange.fromStart < seedRange.end) ||
                                    (transformRange.fromStart < seedRange.start && transformRange.fromEnd > seedRange.end))
                        }.toList()

                    if (filteredTransformMap.isEmpty()) temp.add(seedRange)
                    else {
                        filteredTransformMap.forEach { transformRange ->
                            val newRangeStart =
                                maxOf(seedRange.start, transformRange.fromStart)
                            val newRangeEnd =
                                minOf(seedRange.end, transformRange.fromEnd)

                            val shift = abs(newRangeStart - transformRange.fromStart)
                            val length = newRangeEnd - newRangeStart

                            val transformToRangeStart = transformRange.toStart + shift
                            temp.add(SeedRange(transformToRangeStart, transformToRangeStart + length))

                            if (seedRange.start < transformRange.fromStart) queue.add(
                                SeedRange(
                                    seedRange.start,
                                    transformRange.fromStart - 1
                                )
                            )
                            if (seedRange.end > transformRange.fromEnd) queue.add(
                                SeedRange(
                                    transformRange.fromEnd + 1,
                                    seedRange.end
                                )
                            )
                        }
                    }
                }

                seedRanges = temp
                transformMap.clear()
            }
        }

        return seedRanges.minOf { it.start }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}