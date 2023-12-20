import java.util.function.Predicate

fun main() {

    data class Condition(val predicate: Predicate<Int>, val lessThan: Boolean, val checkedNumber: Int)
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
    data class WorkflowStep(
        val targetChar: Char,
        val condition: Condition,
        val targetWorkflow: String
    )

    data class Workflow(val name: String, val steps: List<WorkflowStep>)

    fun greaterThan(number: Int): (Int) -> Boolean {
        return { it > number }
    }

    fun lesserThan(number: Int): (Int) -> Boolean {
        return { it < number }
    }

    fun alwaysTrue(number: Int): (Int) -> Boolean {
        return { true }
    }

    fun parseInput(input: List<String>): Pair<List<Workflow>, List<Part>> {
        val workflowStrings = input.takeWhile { it.isNotBlank() }
        val partsStrings = input.drop(workflowStrings.size + 1)

        val parts = partsStrings.map { line ->
            val numberRegex = "\\d+".toRegex()
            val (x, m, a, s) = numberRegex.findAll(line).map { it.value.toInt() }.toList()
            Part(x, m, a, s)
        }

        val workflows = workflowStrings.map { line ->
            val (name, stepsString) = line.split('{')
            val workflowSteps = stepsString.dropLast(1).split(',').map { string ->
                val steps = string.split(':')
                val conditionString = if (steps.size == 2) steps[0] else ""
                val (targetChar, condition) = if (conditionString.isNotBlank()) {
                    val number = conditionString.drop(2).toInt()
                    var lessThan = true
                    val predicate: (Int) -> Boolean
                    when (conditionString[1]) {
                        '>' -> {
                            lessThan = false
                            predicate = greaterThan(number)
                        }

                        else -> {
                            predicate = lesserThan(number)
                        }
                    }
                    conditionString[0] to Condition(predicate, lessThan, number)
                } else '.' to Condition(alwaysTrue(0), false, 0)
                val target = if (steps.size == 2) steps[1] else steps[0]
                WorkflowStep(targetChar, condition, target)
            }
            Workflow(name, workflowSteps)
        }

        return workflows to parts
    }

    fun part1(input: List<String>): Int {
        val (workflows, parts) = parseInput(input)
        val accepted = mutableListOf<Part>()
        parts.forEach { part ->
            var currentName = "in"
            while (setOf("R", "A").contains(currentName).not()) {
                val current = workflows.find { it.name == currentName }!!
                run newFlow@{
                    current.steps.forEach {
                        val testedValue = when (it.targetChar) {
                            'x' -> part.x
                            'm' -> part.m
                            'a' -> part.a
                            else -> part.s
                        }
                        if (it.condition.predicate.test(testedValue)) {
                            currentName = it.targetWorkflow
                            return@newFlow
                        }
                    }
                }
                if (currentName == "A") accepted.add(part)
            }
        }
        return accepted.sumOf { it.x + it.a + it.m + it.s }
    }

    fun combinations(ranges: Map<Char, IntRange>): Long {
        return ranges.map { (it.value.last - it.value.first + 1).toLong() }
            .reduce { acc, i -> acc * i }
            .toLong()
    }

    fun part2(input: List<String>): Long {
        val (workflows, _) = parseInput(input)
        val parts = mutableMapOf('x' to 1..4000, 'a' to 1..4000, 'm' to 1..4000, 's' to 1..4000)

        var sum = 0L
        val queue = ArrayDeque<Pair<MutableMap<Char, IntRange>, Workflow>>()
        queue.add(parts.toMutableMap() to workflows.find { it.name == "in" }!!)
        while (queue.isNotEmpty()) {
            val (currentParts, currentFlow) = queue.removeFirst()

            run newFlow@{
                currentFlow.steps.forEach {
                    if (currentParts.keys.contains(it.targetChar)) {
                        val testedValue = it.condition.checkedNumber
                        val positiveRange: IntRange
                        val negativeRange: IntRange
                        val positiveParts: Map<Char, IntRange>
                        val partsRange = currentParts[it.targetChar]!!
                        if (it.condition.lessThan) {
                            positiveRange = partsRange.first..<testedValue
                            negativeRange = testedValue..partsRange.last
                        } else {
                            positiveRange = testedValue + 1..partsRange.last
                            negativeRange = partsRange.first..testedValue
                        }

                        positiveParts = currentParts.toMutableMap()
                        positiveParts[it.targetChar] = positiveRange
                        currentParts[it.targetChar] = negativeRange

                        if (it.targetWorkflow == "A") {
                            sum += combinations(positiveParts)
                        } else if (it.targetWorkflow != "R") {
                            queue.addLast(positiveParts to workflows.find { workflow -> workflow.name == it.targetWorkflow }!!)
                        }
                    } else {
                        if (it.targetWorkflow == "A") {
                            sum += combinations(currentParts)
                        } else if (it.targetWorkflow != "R") {
                            queue.addLast(currentParts to workflows.find { workflow -> workflow.name == it.targetWorkflow }!!)
                        }
                    }
                }
            }
        }

        return sum
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}