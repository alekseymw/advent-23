fun main() {

    fun readData(input: List<String>): Map<Long, Long> {
        val regex = "\\d+".toRegex()
        val timeList = regex.findAll(input[0]).map { it.value.toLong() }.toList()
        val distanceList = regex.findAll(input[1]).map { it.value.toLong() }.toList()
        return (timeList.indices).associate { timeList[it] to distanceList[it] }
    }

    fun readData2(input: List<String>): Pair<Long, Long> {
        val regex = "\\D".toRegex()
        val time = input[0].replace(regex, "").toLong()
        val distance = input[1].replace(regex, "").toLong()
        return time to distance
    }

    fun calculateWins(time: Long, distance: Long) = (0..time / 2).count { (time - it) * it > distance }

    fun part1(input: List<String>): Long {
        val timeToDistanceMap = readData(input)
        return timeToDistanceMap.map { calculateWins(it.key, it.value) * 2 - (1 - it.key % 2) }
            .reduce { acc, i ->
                acc * i
            }
    }

    fun part2(input: List<String>): Long {
        val timeToDistance = readData2(input)
        return calculateWins(timeToDistance.first, timeToDistance.second) * 2 - (1 - timeToDistance.first % 2)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288L)
    check(part2(testInput) == 71503L)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}