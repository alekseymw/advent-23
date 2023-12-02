import kotlin.math.max

fun main() {

    val MAX_RED = 12
    val MAX_GREEN = 13
    val MAX_BLUE = 14

    val regex = "\\s*[,;]\\s*".toRegex()

    fun part1(input: List<String>): Int {
        var result = 0
        var gameNumber = 0
        input.map { it.split(": ")[1] }
            .forEach { gameString ->
                gameNumber++

                var isGamePossible = true

                gameString.split(regex)
                    .forEach {
                        val (number, color) = it.split(" ")
                        when (color) {
                            "red" -> if (number.toInt() > MAX_RED) isGamePossible = false
                            "green" -> if (number.toInt() > MAX_GREEN) isGamePossible = false
                            "blue" -> if (number.toInt() > MAX_BLUE) isGamePossible = false
                        }
                    }

                if (isGamePossible) result += gameNumber
            }
        return result
    }

    fun part2(input: List<String>): Int {
        var result = 0

        input.map { it.split(": ")[1] }
            .forEach { gameString ->
                var redCount = 0
                var greenCount = 0
                var blueCount = 0

                gameString.split(regex)
                    .forEach {
                        val (number, color) = it.split(" ")
                        when (color) {
                            "red" -> redCount = max(redCount, number.toInt())
                            "green" -> greenCount = max(greenCount, number.toInt())
                            "blue" -> blueCount = max(blueCount, number.toInt())
                        }
                    }

                result += redCount * greenCount * blueCount
            }
        return result
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}