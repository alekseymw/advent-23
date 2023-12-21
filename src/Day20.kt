enum class Signal {
    HIGH, LOW
}

interface Module {

    fun name(): String

    fun receivers(): MutableList<Module>

    fun receiverNames(): List<String>

    fun sentSignals(): Map<Signal, Int>

    fun accept(pulse: Pair<String, Signal>, arrayDeque: ArrayDeque<Triple<String, Module, Signal>>)
}

data class FlipFlop(
    var on: Boolean = false,
    val name: String,
    val receivers: MutableList<Module>,
    val receiverNames: List<String>,
    val sentSignals: MutableMap<Signal, Int> = mutableMapOf()
) : Module {
    override fun name() = name

    override fun receivers() = receivers

    override fun receiverNames() = receiverNames

    override fun sentSignals(): Map<Signal, Int> = sentSignals

    override fun accept(pulse: Pair<String, Signal>, arrayDeque: ArrayDeque<Triple<String, Module, Signal>>) {
        if (pulse.second == Signal.LOW) {
            val outputSignal = if (on) Signal.LOW else Signal.HIGH
            on = on.not()
            receivers.forEach { arrayDeque.add(Triple(name, it, outputSignal)) }
            sentSignals.merge(outputSignal, receivers.size) { old, _ -> old + receivers.size }
        }
    }

    override fun toString(): String {
        return "FlipFlop [name=$name, on=$on, receivers=${receiverNames.joinToString(','.toString())}, sent=$sentSignals]"
    }
}

data class Conjunction(
    val memory: MutableMap<String, Signal> = mutableMapOf(),
    val name: String,
    val receivers: MutableList<Module>,
    val receiverNames: List<String>,
    val sentSignals: MutableMap<Signal, Int> = mutableMapOf()
) : Module {
    override fun name() = name
    override fun receivers() = receivers

    override fun receiverNames() = receiverNames

    override fun sentSignals(): Map<Signal, Int> = sentSignals

    override fun accept(pulse: Pair<String, Signal>, arrayDeque: ArrayDeque<Triple<String, Module, Signal>>) {
        memory[pulse.first] = pulse.second
        val outputSignal = if (memory.all { it.value == Signal.HIGH }) Signal.LOW else Signal.HIGH
        this.receivers.forEach { arrayDeque.add(Triple(name, it, outputSignal)) }
        sentSignals.merge(outputSignal, receivers.size) { old, _ -> old + receivers.size }
    }


    override fun toString(): String {
        return "Conjunction [name=$name, receivers=${receiverNames.joinToString(','.toString())}, sent=$sentSignals]"
    }
}

data class Broadcaster(
    val name: String,
    val receivers: MutableList<Module>,
    val receiverNames: List<String>,
    val sentSignals: MutableMap<Signal, Int> = mutableMapOf()
) : Module {

    override fun name() = name

    override fun receivers() = receivers

    override fun receiverNames() = receiverNames

    override fun sentSignals(): Map<Signal, Int> = sentSignals

    override fun accept(pulse: Pair<String, Signal>, arrayDeque: ArrayDeque<Triple<String, Module, Signal>>) {
        val signal = pulse.second
        receivers.forEach { arrayDeque.add(Triple(name, it, signal)) }
        sentSignals.merge(signal, receivers.size) { old, _ -> old + receivers.size }
    }

    override fun toString(): String {
        return "Broadcaster [receivers=${receiverNames.joinToString(','.toString())}, sent=$sentSignals]"
    }
}

data class Output(val name: String) : Module {
    override fun name() = name

    override fun receivers() = mutableListOf<Module>()

    override fun receiverNames() = emptyList<String>()

    override fun sentSignals() = emptyMap<Signal, Int>()

    override fun accept(pulse: Pair<String, Signal>, arrayDeque: ArrayDeque<Triple<String, Module, Signal>>) {

    }
}

fun main() {

    fun parseInput(input: List<String>): List<Module> {
        val modules = input.map { line ->
            val (namePart, receiversPart) = line.split(" -> ")
            val receiverNames = receiversPart.split(',').map { it.trim() }
            val type = namePart[0]
            val name = if (namePart == "broadcaster") namePart else namePart.drop(1)
            val module = when (type) {
                '%' -> FlipFlop(name = name, receivers = mutableListOf(), receiverNames = receiverNames)
                '&' -> Conjunction(name = name, receivers = mutableListOf(), receiverNames = receiverNames)
                else -> Broadcaster(name = name, receivers = mutableListOf(), receiverNames = receiverNames)
            }
            module
        }

        modules.forEach { module ->
            module.receiverNames().forEach { name ->
                module.receivers().add(modules.find { it.name() == name } ?: Output(name))
            }
        }

        modules.forEach { module ->
            module.receivers().filterIsInstance<Conjunction>()
                .forEach { it.memory[module.name()] = Signal.LOW }
        }

        return modules
    }

    fun part1(input: List<String>): Int {
        val modules = parseInput(input)
        val broadcaster = modules.find { it is Broadcaster }!!
        val arrayDeque = ArrayDeque<Triple<String, Module, Signal>>()
        repeat(1000) {
            arrayDeque.add(Triple("button", broadcaster, Signal.LOW))
            while (arrayDeque.isNotEmpty()) {
                val (from, module, signal) = arrayDeque.removeFirst()
                module.accept(from to signal, arrayDeque)
            }

        }
        val low = modules.map { it.sentSignals() }.sumOf { it[Signal.LOW] ?: 0 } + 1000
        val high = modules.map { it.sentSignals() }.sumOf { it[Signal.HIGH] ?: 0 }
        return low * high
    }

    fun part2(input: List<String>): Long {
        val modules = parseInput(input)
        val broadcaster = modules.find { it is Broadcaster }!!
        val targetConjunction = modules.find { it.receiverNames().contains("rx") }!! as Conjunction
        val arrayDeque = ArrayDeque<Triple<String, Module, Signal>>()
        var found = false
        var presses = 0L
        val inputCycles = mutableMapOf<String, Long>()
        while (found.not()) {
            presses++
            arrayDeque.add(Triple("button", broadcaster, Signal.LOW))
            while (arrayDeque.isNotEmpty()) {
                val (from, module, signal) = arrayDeque.removeFirst()
                if (module.name() == targetConjunction.name && signal == Signal.HIGH) {
                    inputCycles.merge(from, presses) { old, _ -> presses - old }
                    if (inputCycles.size == targetConjunction.memory.size) {
                        found = true
                        break
                    }
                }
                module.accept(from to signal, arrayDeque)
            }
        }
        return inputCycles.values.reduce { acc, l -> lcm(acc, l) }
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 32000000)
    val testInput2 = readInput("Day20_test2")
    check(part1(testInput2) == 11687500)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}