fun main() {

    fun parseInput(input: List<String>): List<List<String>> {
        val result = mutableListOf<List<String>>()
        var list = input.takeWhile { it.isNotBlank() }
        var rest = input
        while (list.isNotEmpty()) {
            result.add(list)
            rest = rest.drop(list.size + 1)
            list = rest.takeWhile { it.isNotBlank() }
        }
        return result
    }

    fun rotateList(array: Array<CharArray>) = buildList<List<Char>> {
        for (i in array[0].indices) {
            val row = mutableListOf<Char>()
            for (j in array.indices) {
                row.add(array[j][i])
            }
            add(row)
        }
    }.map { it.joinToString("") }.toList()

    fun differenceEqualsOneElement(first: String, second: String): Boolean {
        if (first.length != second.length) return false
        var count = 0
        for (i in first.indices) {
            if (first[i] != second[i]) count++
            if (count > 1) return false
        }
        return count == 1
    }

    fun checkIfMirror(input: List<String>, allowedError: Boolean = false): Boolean {
        var hasError = !allowedError
        for (i in input.indices) {
            val j = input.size - 1 - i
            if (input[i] != input[j]) {
                if (!allowedError) return false

                val differenceEqualsOneElement = differenceEqualsOneElement(input[i], input[j])
                if (differenceEqualsOneElement.not())
                    return false
                else {
                    if (hasError) return false
                    hasError = true
                }
            }
            if (i == j - 1 && hasError) return true
        }
        return false
    }

    fun findMirrorLine(input: List<String>, allowedError: Boolean = false): Triple<Int, Int, Int>? {

        val possibleMirrors = mutableSetOf<Triple<Int, Int, Int>>()
        for (i in input.indices) {
            for (j in i + 1 until input.size) {
                if (checkIfMirror(input.subList(i, j + 1), allowedError)) {
                    possibleMirrors.add(Triple(i, j, (j + i) / 2 + 1))
                }
            }
        }

        return possibleMirrors.firstOrNull {
            (it.first >= 1 && it.second == input.size - 1) || (it.first == 0 && it.second <= input.size - 1)
        }
    }

    fun part1(input: List<String>): Int {
        val inputData = parseInput(input)

        return inputData.sumOf {
            val horizontal = findMirrorLine(it)
            val vertical = findMirrorLine(rotateList(it.map { it.toCharArray() }.toTypedArray()))
            val result = vertical?.third ?: horizontal?.third?.times(100) ?: 0
            result
        }
    }

    fun part2(input: List<String>): Int {
        val inputData = parseInput(input)

        return inputData.sumOf {
            val horizontal = findMirrorLine(it, true)
            val vertical = findMirrorLine(rotateList(it.map { it.toCharArray() }.toTypedArray()), true)
            val result = horizontal?.third?.times(100) ?: vertical?.third ?: 0
            result
        }
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}