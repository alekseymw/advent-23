import java.util.function.Function
import kotlin.math.pow

fun part1(input: List<String>): Int {
    val cardStrength = "23456789TJQKA"
    val sorted = sortByStrength(input, cardStrength, ::handStrength)
    return sorted.mapIndexed { index, pair -> pair.second * (index + 1) }
        .sum()
}

fun part2(input: List<String>): Int {
    val cardStrength = "J23456789TQKA"
    val sorted = sortByStrength(input, cardStrength, ::handStrengthWithJokers)
    return sorted.mapIndexed { index, pair -> pair.second * (index + 1) }
        .sum()
}

fun handStrength(hand: String): Int {
    val map = mutableMapOf<Char, Int>()
    for (card in hand) {
        map.merge(card, 1, Int::plus)
    }
    return map.map { it.value.toDouble().pow(2) }.sum().toInt()
}

fun handStrengthWithJokers(hand: String): Int {
    val map = mutableMapOf<Char, Int>()
    var j = 0
    for (card in hand) {
        if (card == 'J') j++ else map.merge(card, 1, Int::plus)
    }
    if (map.isEmpty()) map['J'] = j else {
        val max = map.maxBy { it.value }
        map[max.key] = max.value + j
    }
    return map.map { it.value.toDouble().pow(2) }.sum().toInt()
}

fun parseInput(input: List<String>): List<Pair<String, Int>> {
    return input.map {
        val (hand, bid) = it.split(" ")
        hand to bid.toInt()
    }
}

private fun sortByStrength(
    input: List<String>,
    cardStrength: String,
    handStrengthFunction: Function<String, Int>
) = parseInput(input).sortedWith { cur, other ->
    val curStr = handStrengthFunction.apply(cur.first)
    val otherStr = handStrengthFunction.apply(other.first)
    var result = 0

    if (curStr != otherStr) result = curStr.compareTo(otherStr)
    else {
        for (i in cur.first.indices) if (cardStrength.indexOf(cur.first[i])
                .compareTo(cardStrength.indexOf(other.first[i])) != 0
        ) {
            result = cardStrength.indexOf(cur.first[i]).compareTo(cardStrength.indexOf(other.first[i]))
            break
        }
    }
    result
}

fun main() {
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    val testInput2 = readInput("Day07_test2")
    check(part2(testInput2) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
