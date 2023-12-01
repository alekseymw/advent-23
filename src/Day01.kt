import kotlin.math.max
import kotlin.math.min

val NUMBER_DICTIONARY = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
    "zero" to 0
)

fun main() {

    fun getCalibrationValue(inputString: String) =
        "${inputString.find { it.isDigit() }}" + "${inputString.findLast { it.isDigit() }}"

    fun part1(input: List<String>): Int {
        return input.map { getCalibrationValue(it) }
            .sumOf { it.toInt() }
    }

    fun getCalibrationValueWithLetterNumbers(inputString: String): Int {
        var letterNumberFirstIndex = Int.MAX_VALUE
        var letterNumberLastIndex = Int.MIN_VALUE
        var letterNumberFirst = -1
        var letterNumberLast = -1

        NUMBER_DICTIONARY.entries.forEach { numberEntry ->
            val firstIndex = inputString.indexOf(numberEntry.key, ignoreCase = true)
            if (firstIndex != -1) {
                if (letterNumberFirstIndex > firstIndex) {
                    letterNumberFirstIndex = min(letterNumberFirstIndex, firstIndex)
                    letterNumberFirst = numberEntry.value
                }

                val lastIndex = inputString.lastIndexOf(numberEntry.key, ignoreCase = true)
                if (letterNumberLastIndex < lastIndex) {
                    letterNumberLastIndex = max(letterNumberLastIndex, lastIndex)
                    letterNumberLast = numberEntry.value
                }
            }
        }

        var firstNumber = inputString.find { it.isDigit() }
        var lastNumber = inputString.findLast { it.isDigit() }

        firstNumber = if (firstNumber == null || inputString.indexOf(firstNumber) > letterNumberFirstIndex) letterNumberFirst.digitToChar() else firstNumber
        lastNumber = if (lastNumber == null || inputString.lastIndexOf(lastNumber) < letterNumberLastIndex) letterNumberLast.digitToChar() else lastNumber

        return "$firstNumber$lastNumber".toInt()
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { getCalibrationValueWithLetterNumbers(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
