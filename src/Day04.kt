import kotlin.math.pow

fun main() {

    data class ScratchCard(val cardNumber: Int, val winningNumbers: List<Int>, val availableNumbers: List<Int>)

    fun parseData(input: List<String>): List<ScratchCard> {
        return input.map { cardString ->
            val numberRegex = "\\d+".toRegex()
            val (gamePart, numberPart) = cardString.split(": ")
            val cardNumber = numberRegex.find(gamePart)?.value?.toInt() ?: 0

            val (winningNumbersString, availableNumbersString) = numberPart.split(" | ")

            val winningNumbers = numberRegex.findAll(winningNumbersString).map { it.value.toInt() }.toList()
            val availableNumbers = numberRegex.findAll(availableNumbersString).map { it.value.toInt() }.toList()

            ScratchCard(cardNumber, winningNumbers, availableNumbers)
        }.toList()
    }

    fun part1(input: List<String>): Int {
        return parseData(input).sumOf {
            val matchingNumbers = it.availableNumbers.intersect(it.winningNumbers.toSet())
            if (matchingNumbers.isNotEmpty()) 2.0.pow(matchingNumbers.size.toDouble() - 1).toInt() else 0
        }
    }

    fun part2(input: List<String>): Int {
        val numberOfScratchCards = Array(input.size) { 1 }
        parseData(input).forEachIndexed { index, scratchCard ->
            val matchingNumbers = scratchCard.availableNumbers.intersect(scratchCard.winningNumbers.toSet())
            for (i in index + 1..index+matchingNumbers.size) numberOfScratchCards[i] += numberOfScratchCards[index]
        }
        return numberOfScratchCards.sum()
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}