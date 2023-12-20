import kotlin.math.abs

fun main() {

    data class DigPlan(val direction: Direction, val length: Int, val color: String)
    data class Point(val x: Int, val y: Int, val color: String)

    fun getPoints(digPlans: List<DigPlan>): List<Point> {
        var x = 0
        var y = 0
        return buildList {
            digPlans.map { plan ->
                repeat(plan.length) {
                    add(Point(x, y, plan.color))
                    x += plan.direction.dx
                    y += plan.direction.dy
                }
            }
        }
    }

    fun parseInput(input: List<String>): List<DigPlan> {
        return input.map { line ->
            val (directionString, lengthString, colorString) = line.split(" ")
            val direction = when (directionString) {
                "R" -> Direction.RIGHT
                "L" -> Direction.LEFT
                "U" -> Direction.UP
                else -> Direction.DOWN
            }
            DigPlan(direction, lengthString.toInt(), colorString.drop(2).dropLast(1))
        }
    }

    fun getArea(points: List<Point>): Double {
        var sum = 0.0

        for (i in points.indices) {
            val j = (i + 1) % points.size
            sum += (points[i].x * points[j].y) - (points[j].x * points[i].y)
        }

        return abs(sum) / 2
    }

    fun part1(input: List<String>): Long {
        val digPlans = parseInput(input)
        val points = getPoints(digPlans)

        val sum = getArea(points)

        return sum.toLong() + (points.size + 2) / 2
    }

    fun updatedPlans(digPlans: List<DigPlan>) = digPlans.map { plan ->
        val length = plan.color.dropLast(1).toInt(radix = 16)
        val directionNumber = plan.color.last().digitToInt()
        val direction = when (directionNumber) {
            0 -> Direction.RIGHT
            1 -> Direction.DOWN
            2 -> Direction.LEFT
            else -> Direction.UP
        }
        DigPlan(direction, length, plan.color)
    }

    fun part2(input: List<String>): Long {
        val digPlans = parseInput(input)
        val newDigPlans = updatedPlans(digPlans)
        val points = getPoints(newDigPlans)
        val sum = getArea(points)
        return sum.toLong() + (points.size + 2) / 2
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}