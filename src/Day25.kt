import java.util.Random

fun main() {

    fun parseInput(input: List<String>): MutableMap<String, MutableList<String>> {
        val vertices = mutableMapOf<String, MutableList<String>>()
        input.forEach { line ->
            val (name, neighbors) = line.split(": ")
            val neighborsList = neighbors.split(" ")
            val neighborsForVertex = vertices.computeIfAbsent(name) { mutableListOf() }
            neighborsList.forEach {
                val neighborVertexList = vertices.computeIfAbsent(it) { mutableListOf() }
                if (neighborsForVertex.contains(it).not()) neighborsForVertex.add(it)
                if (neighborVertexList.contains(name).not()) neighborVertexList.add(name)
            }
        }
        return vertices
    }

    /**
     * Credit goes to reddit user encse from /r/adventofcode
     */
    fun findCut(input: List<String>, r: Random): Triple<Int, Int, Int> {
        val graph = parseInput(input)
        val componentSize = graph.keys.associateWith { 1 }.toMutableMap()

        val rebind: (String, String, String) -> Unit = { oldNode, newNode, ignoreNode ->
            graph[oldNode]?.let { oldNodeNeighbours ->
                oldNodeNeighbours.toList().forEach { neighbour ->
                    if (neighbour != ignoreNode) {
                        graph[neighbour]?.apply {
                            remove(oldNode)
                            add(newNode)
                        }
                    }
                }
            }
        }

        var id = 0
        while (graph.size > 2) {
            val u = graph.keys.elementAt(r.nextInt(graph.size))
            val v = graph[u]?.elementAt(r.nextInt(graph[u]?.size ?: 0))!!

            val merged = "merge-$id"
            graph[merged] = ((graph[u]?.filter { it != v } ?: emptyList()) +
                    (graph[v]?.filter { it != u } ?: emptyList())).toMutableList()
            rebind(u, merged, v)
            rebind(v, merged, u)

            graph.remove(u)
            graph.remove(v)

            componentSize[merged] = (componentSize[u] ?: 0) + (componentSize[v] ?: 0)
            id++
        }

        val nodeA = graph.keys.first()
        val nodeB = graph.keys.last()
        return Triple(graph[nodeA]?.size ?: 0, componentSize[nodeA] ?: 0, componentSize[nodeB] ?: 0)
    }

    fun part1(input: List<String>): Int {
        val r = Random()
        var triple = findCut(input, r)
        while (triple.first != 3) {
            triple = findCut(input, r)
        }
        return triple.second * triple.third
    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == 54)

    val input = readInput("Day25")
    part1(input).println()
}