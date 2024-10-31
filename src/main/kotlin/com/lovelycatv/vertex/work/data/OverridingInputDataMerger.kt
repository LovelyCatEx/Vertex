package com.lovelycatv.vertex.work.data

/**
 * @author lovelycat
 * @since 2024-10-31 21:41
 * @version 1.0
 */
class OverridingInputDataMerger(private val determine: (key: String, producerIndex: Int) -> Boolean) : InputWorkDataMerger {
    override fun merge(results: List<WorkData>): WorkData {
        val pairs = results.flatMap { it.toPairList() }
        val keySet = pairs.map { it.first }.toSet()

        val fxSearchKeyIn = fun (key: String): List<Int> {
            val result = mutableListOf<Int>()
            results.forEachIndexed { index, workData ->
                if (workData.keys.contains(key)) {
                    result.add(index)
                }
            }
            return result
        }

        val savedPairs = mutableListOf<Pair<String, Any?>>()

        keySet.forEach { key ->
            val existingIndexes = fxSearchKeyIn(key)
            if (existingIndexes.size > 1) {
                existingIndexes.forEach { producerIndex ->
                    if (determine(key, producerIndex)) {
                        savedPairs.add(key to results[producerIndex][key])
                    }
                }
            } else {
                savedPairs.add(key to results[existingIndexes[0]][key])
            }
        }

        return WorkData.build(savedPairs)
    }
}