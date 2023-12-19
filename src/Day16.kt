fun main() {

    data class Visited(val from: MutableSet<Direction>)

    fun traverse(x: Int, y: Int, direction: Direction, grid: Array<CharArray>, visited: Array<Array<Visited>>) {
        if (x < 0 || x >= grid[0].size || y < 0 || y >= grid.size || visited[y][x].from.contains(direction)) return

        visited[y][x].from.add(direction)

        when (direction) {
            Direction.UP -> {
                when (grid[y][x]) {
                    '.', '|' -> traverse(x, y - 1, Direction.UP, grid, visited)
                    '/' -> traverse(x + 1, y, Direction.RIGHT, grid, visited)
                    '\\' -> traverse(x - 1, y, Direction.LEFT, grid, visited)
                    '-' -> {
                        traverse(x + 1, y, Direction.RIGHT, grid, visited)
                        traverse(x - 1, y, Direction.LEFT, grid, visited)
                    }
                }
            }

            Direction.DOWN -> {
                when (grid[y][x]) {
                    '.', '|' -> traverse(x, y + 1, Direction.DOWN, grid, visited)
                    '/' -> traverse(x - 1, y, Direction.LEFT, grid, visited)
                    '\\' -> traverse(x + 1, y, Direction.RIGHT, grid, visited)
                    '-' -> {
                        traverse(x + 1, y, Direction.RIGHT, grid, visited)
                        traverse(x - 1, y, Direction.LEFT, grid, visited)
                    }
                }
            }

            Direction.LEFT -> {
                when (grid[y][x]) {
                    '.', '-' -> traverse(x - 1, y, Direction.LEFT, grid, visited)
                    '/' -> traverse(x, y + 1, Direction.DOWN, grid, visited)
                    '\\' -> traverse(x, y - 1, Direction.UP, grid, visited)
                    '|' -> {
                        traverse(x, y - 1, Direction.UP, grid, visited)
                        traverse(x, y + 1, Direction.DOWN, grid, visited)
                    }
                }
            }

            Direction.RIGHT -> {
                when (grid[y][x]) {
                    '.', '-' -> traverse(x + 1, y, Direction.RIGHT, grid, visited)
                    '/' -> traverse(x, y - 1, Direction.UP, grid, visited)
                    '\\' -> traverse(x, y + 1, Direction.DOWN, grid, visited)
                    '|' -> {
                        traverse(x, y - 1, Direction.UP, grid, visited)
                        traverse(x, y + 1, Direction.DOWN, grid, visited)
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }.toTypedArray()
        val visited = Array(grid.size) { Array(grid[0].size) { Visited(mutableSetOf()) } }
        traverse(x = 0, y = 0, Direction.RIGHT, grid, visited)
        return visited.sumOf { it.count { it.from.isNotEmpty() } }
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }.toTypedArray()

        val topMax = grid[0].indices.maxOf {
            val visited = Array(grid.size) { Array(grid[0].size) { Visited(mutableSetOf()) } }
            traverse(x = it, y = 0, Direction.DOWN, grid, visited)
            visited.sumOf { visit -> visit.count { node -> node.from.isNotEmpty() } }
        }

        val bottomMax = grid[0].indices.maxOf {
            val visited = Array(grid.size) { Array(grid[0].size) { Visited(mutableSetOf()) } }
            traverse(x = it, y = grid.size - 1, Direction.UP, grid, visited)
            visited.sumOf { visit -> visit.count { node -> node.from.isNotEmpty() } }
        }

        val leftMax = grid.indices.maxOf {
            val visited = Array(grid.size) { Array(grid[0].size) { Visited(mutableSetOf()) } }
            traverse(x = 0, y = it, Direction.RIGHT, grid, visited)
            visited.sumOf { visit -> visit.count { node -> node.from.isNotEmpty() } }
        }

        val rightMax = grid.indices.maxOf {
            val visited = Array(grid.size) { Array(grid[0].size) { Visited(mutableSetOf()) } }
            traverse(x = grid.size - 1, y = it, Direction.LEFT, grid, visited)
            visited.sumOf { visit -> visit.count { node -> node.from.isNotEmpty() } }
        }

        return maxOf(topMax, bottomMax, leftMax, rightMax)
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}