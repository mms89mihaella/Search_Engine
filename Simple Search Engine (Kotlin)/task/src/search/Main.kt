import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] != "--data" || args.size < 2) {
        println("Usage: --data <filename>")
        return
    }

    val filename = args[1]
    val lines = File(filename).readLines()
    val invertedIndex = buildInvertedIndex(lines)

    while (true) {
        println("\n=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")

        when (readln().trim()) {
            "1" -> searchPeople(lines, invertedIndex)
            "2" -> printAllPeople(lines)
            "0" -> {
                println("Bye!")
                return
            }
            else -> println("Incorrect option! Try again.")
        }
    }
}

fun buildInvertedIndex(lines: List<String>): Map<String, MutableSet<Int>> {
    val index = mutableMapOf<String, MutableSet<Int>>()

    lines.forEachIndexed { lineIndex, line ->
        line.split(Regex("\\s+")).forEach { word ->
            val key = word.lowercase()
            index.computeIfAbsent(key) { mutableSetOf() }.add(lineIndex)
        }
    }

    return index
}

fun searchPeople(lines: List<String>, invertedIndex: Map<String, MutableSet<Int>>) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = readln().trim().uppercase()

    println("Enter a name or email to search all matching people.")
    val queryWords = readln().trim().lowercase().split(Regex("\\s+"))

    val resultIndices = when (strategy) {
        "ALL" -> searchAll(queryWords, invertedIndex, lines.size)
        "ANY" -> searchAny(queryWords, invertedIndex)
        "NONE" -> searchNone(queryWords, invertedIndex, lines.size)
        else -> {
            println("Unknown strategy! Please try again.")
            return
        }
    }

    if (resultIndices.isEmpty()) {
        println("No matching people found.")
    } else {
        println("${resultIndices.size} person(s) found:")
        resultIndices.forEach { index -> println(lines[index]) }
    }
}

fun searchAll(queryWords: List<String>, invertedIndex: Map<String, MutableSet<Int>>, totalLines: Int): Set<Int> {
    return queryWords.fold((0 until totalLines).toSet()) { acc, word ->
        acc.intersect(invertedIndex[word].orEmpty())
    }
}

fun searchAny(queryWords: List<String>, invertedIndex: Map<String, MutableSet<Int>>): Set<Int> {
    return queryWords.flatMap { word -> invertedIndex[word].orEmpty() }.toSet()
}

fun searchNone(queryWords: List<String>, invertedIndex: Map<String, MutableSet<Int>>, totalLines: Int): Set<Int> {
    val allIndices = (0 until totalLines).toSet()
    val queryIndices = queryWords.flatMap { word -> invertedIndex[word].orEmpty() }.toSet()
    return allIndices.subtract(queryIndices)
}

fun printAllPeople(lines: List<String>) {
    println("\n=== List of people ===")
    lines.forEach { println(it) }
}
