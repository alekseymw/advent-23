fun main() {

    data class EnginePart(
        val amount: Int,
        val rowNumber: Int,
        val positionStart: Int,
        val positionEnd: Int
    )

    fun getNumberList(input: List<String>) = input.flatMapIndexed { rowNumber, inputString ->
        val regex = "\\d+".toRegex()
        regex.findAll(inputString, 0).map {
            EnginePart(it.value.toInt(), rowNumber, it.range.first, it.range.last)
        }.toList()
    }

    fun adjacentParts(
        numberList: List<EnginePart>,
        rowNumberOfSymbol: Int,
        indexOfSymbol: Int
    ) = numberList.filter {
        it.rowNumber in rowNumberOfSymbol - 1..rowNumberOfSymbol + 1 && indexOfSymbol in it.positionStart - 1..it.positionEnd + 1
    }

    fun sumAdjacentParts(numberList: List<EnginePart>, rowNumberOfSymbol: Int, indexOfSymbol: Int) = adjacentParts(
        numberList,
        rowNumberOfSymbol,
        indexOfSymbol
    ).sumOf { it.amount }

    fun countRatioOfGear(numberList: List<EnginePart>, rowNumberOfSymbol: Int, indexOfSymbol: Int): Int {
        val adjacentParts = adjacentParts(numberList, rowNumberOfSymbol, indexOfSymbol)
        return if (adjacentParts.size == 2) {
            adjacentParts[0].amount * adjacentParts[1].amount
        } else 0
    }


    fun part1(input: List<String>): Int {
        val numberList = getNumberList(input)

        return input.mapIndexed { rowNumberOfSymbol, inputString ->
            var result = 0
            for (i in inputString.indices) {
                if (inputString[i].isDigit().not() && inputString[i] != '.') {
                    result += sumAdjacentParts(numberList, rowNumberOfSymbol, i)
                }
            }
            result
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val numberList = getNumberList(input)

        return input.mapIndexed { rowNumberOfSymbol, inputString ->
            var ratioOfGear = 0
            for (i in inputString.indices) {
                if (inputString[i] == '*') {
                    ratioOfGear += countRatioOfGear(numberList, rowNumberOfSymbol, i)
                }
            }
            ratioOfGear
        }.sum()
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}