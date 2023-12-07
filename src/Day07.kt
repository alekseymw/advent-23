import kotlin.math.pow

enum class CardEnum(val stringValue: String) {
    CA("A"),
    CK("K"),
    CQ("Q"),
    CJ("J"),
    CT("T"),
    C9("9"),
    C8("8"),
    C7("7"),
    C6("6"),
    C5("5"),
    C4("4"),
    C3("3"),
    C2("2")
}

fun handStrength(hand: String): Int {
    val map = mutableMapOf<Char, Int>()
    for (card in hand) {
        map.merge(card, 1, Int::plus)
    }
    return map.map { 2.0.pow(it.value) }.sum().toInt()
}

fun parseInput(input: List<String>): List<Pair<String, Int>> {
    return input.map {
        val (hand, bid) = it.split(" ")
        hand to bid.toInt()
    }
}

fun part1(input: List<String>): Int {
    val sorted =
        parseInput(input).sortedWith(compareBy<Pair<String, Int>> { handStrength(it.first) }
            .thenComparator { cur, other ->
                val curList = cur.first.map { CardEnum.valueOf("C$it") }.toList()
                val otherList = other.first.map { CardEnum.valueOf("C$it") }.toList()
                var result = 0
                for (i in curList.indices) if (curList[i].compareTo(otherList[i]) != 0) {
                    result = curList[i].compareTo(otherList[i])
                    break
                }
                -result
            })

    parseInput(input).sortedWith(Comparator { cur, other ->
        val curStr = handStrength(cur.first)
        val otherStr = handStrength(other.first)
        var result = 0

        if (curStr != otherStr) result = curStr.compareTo(otherStr)
        else {
            val curList = cur.first.map { CardEnum.valueOf("C$it") }.toList()
            val otherList = other.first.map { CardEnum.valueOf("C$it") }.toList()
            for (i in curList.indices) if (curList[i].compareTo(otherList[i]) != 0) {
                result = curList[i].compareTo(otherList[i])
                break
            }
        }
        result
    })
    return sorted.mapIndexed { index, pair -> pair.second * (index + 1) }
        .sum()
}

fun main() {
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)

    val input = readInput("Day07")
    part1(input).println()
}
