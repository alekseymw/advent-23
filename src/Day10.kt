fun main() {

    lateinit var start: Pair<Int, Int>

    fun canMove(startI: Int, startJ: Int, previousMove: String, input: List<String>, direction: String): Boolean {
        return when (direction) {
            "LEFT" -> previousMove != "RIGHT" && startJ > 0 && (input[startI][startJ - 1] == '-' || input[startI][startJ - 1] == 'F' || input[startI][startJ - 1] == 'L')
            "RIGHT" -> previousMove != "LEFT" && startJ < input[startI].length - 1 && (input[startI][startJ + 1] == '-' || input[startI][startJ + 1] == '7' || input[startI][startJ + 1] == 'J')
            "UP" -> previousMove != "DOWN" && startI > 0 && (input[startI - 1][startJ] == '|' || input[startI - 1][startJ] == 'F' || input[startI - 1][startJ] == '7')
            "DOWN" -> previousMove != "UP" && startI < input.size - 1 && (input[startI + 1][startJ] == '|' || input[startI + 1][startJ] == 'L' || input[startI + 1][startJ] == 'J')
            "START" -> true
            else -> false
        }
    }

    fun findLoop(startI: Int, startJ: Int, input: List<String>, results: Array<IntArray>, previousMove: String) {
        var i = startI
        var j = startJ
        var distance = 1
        var previous = previousMove
        while (results[i][j] == 0) {
            results[i][j] = distance++
            if (i == start.first && j == start.second) break

            if (setOf('-', 'L', 'F').contains(input[i][j]) && canMove(i, j, previous, input, "RIGHT")) {
                j++
                previous = "RIGHT"
            } else if (setOf('-', 'J', '7').contains(input[i][j]) && canMove(i, j, previous, input, "LEFT")) {
                j--
                previous = "LEFT"
            } else if (setOf('|', 'L', 'J').contains(input[i][j]) && canMove(i, j, previous, input, "UP")) {
                i--
                previous = "UP"
            } else if (setOf('|', 'F', '7').contains(input[i][j]) && canMove(i, j, previous, input, "DOWN")) {
                i++
                previous = "DOWN"
            }
        }
    }

    fun getStart(input: List<String>): Pair<Int, Int> {
        var startI = 0
        var startJ = 0
        for (i in input.indices) {
            for (j in input[i].indices)
                if (input[i][j] == 'S') {
                    startI = i
                    startJ = j
                }
        }
        return Pair(startI, startJ)
    }

    fun markLoop(startI: Int, startJ: Int, input: List<String>, results: Array<IntArray>) {
        if (canMove(startI, startJ, "START", input, "LEFT")) findLoop(startI, startJ - 1, input, results, "LEFT") else
        if (canMove(startI, startJ, "START", input, "RIGHT")) findLoop(startI, startJ + 1, input, results, "RIGHT") else
        if (canMove(startI, startJ, "START", input, "UP")) findLoop(startI - 1, startJ, input, results, "UP") else
        if (canMove(startI, startJ, "START", input, "DOWN")) findLoop(startI + 1, startJ, input, results, "DOWN")
    }

    fun part1(input: List<String>): Int {
        start = getStart(input)
        val results = Array(input.size) { IntArray(input[0].length) { 0 } }
        markLoop(start.first, start.second, input, results)
        return (results.maxOf { it.max() } + 1) / 2
    }

    fun visitNode(results: Array<IntArray>, visited: Array<BooleanArray>, i: Int, j: Int) {
        if (i < 0 || i >= results.size || j < 0 || j >= results[i].size || visited[i][j] || results[i][j] != 0) return

        visited[i][j] = true

        visitNode(results, visited, i - 1, j)
        visitNode(results, visited, i + 1, j)
        visitNode(results, visited, i, j - 1)
        visitNode(results, visited, i, j + 1)
        visitNode(results, visited, i - 1, j - 1)
        visitNode(results, visited, i - 1, j + 1)
        visitNode(results, visited, i + 1, j - 1)
        visitNode(results, visited, i + 1, j + 1)
    }

    fun part2(input: List<String>): Int {
        start = getStart(input)

        val results = Array(input.size) { IntArray(input[0].length) { 0 } }
        markLoop(start.first, start.second, input, results)
        results[start.first][start.second] = 1
        val visited = Array(input.size) { BooleanArray(input[0].length) { false } }

        for (i in results.indices)
            for (j in results[i].indices) {
                if (i == 0 || j == 0 || i == input.size - 1 || j == input[i].length - 1) visitNode(results, visited, i, j)
            }

        results[start.first][start.second] = 1

        var count = 0
        for (i in results.indices) {
            var inside = false
            var prev = '_'
            for (j in results[i].indices) {
                if ("|JL7F".contains(input[i][j])) {
                    if (prev == '_') {
                        prev = input[i][j]
                    } else
                    if (prev == '|' && "|JL7F".contains(input[i][j])) {
                        prev = input[i][j]
                        inside = inside.not()
                    } else
                        if (prev == 'J' && "|7JF".contains(input[i][j])) {
                            prev = input[i][j]
                            inside = inside.not()
                        }
                        else
                        if (prev == 'L' && "|FJ".contains(input[i][j])) {
                            prev = input[i][j]
                            inside = inside.not()
                        }
                        else
                        if (prev == '7' && "|J".contains(input[i][j])) {
                            prev = input[i][j]
                            inside = inside.not()
                        }
                        else
                        if (prev == 'F' && "|FL7".contains(input[i][j])) {
                            prev = input[i][j]
                            inside = inside.not()
                        }
                }
                if (visited[i][j].not() && results[i][j] == 0 && inside) {
                    count++
                }
            }
        }
        return count
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
//    val testInput2 = readInput("Day10_test2")
//    check(part2(testInput2) == 10)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}