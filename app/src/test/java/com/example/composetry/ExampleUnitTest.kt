package com.example.composetry

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.ServerSocket

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect2() {
        print("test yello")
        println("test yello")
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getPorts(){
        val portList= 25565..65535
        for (port in portList) {
            try {
                if(isLocalPortFree(port)) {
                    println("port $port is free")
                }
            } catch (ignored: IOException) {
            }
        }
    }

    private fun isLocalPortFree(port: Int): Boolean {
        return try {
            ServerSocket(port).close()
            true
        } catch (e: IOException) {
            false
        }
    }

    @Test
    fun addition_isCorrect() {
        val array = arrayOf(1, 32, 5, 135)
        val og = array.copyOf()

        var indexToCorrectOrder = -1

        if (array.isEmpty()) {
            println("empty")
        } else if (array.size < 2) {
            println("No two elements")
        } else {
            val firstWholeArray = array[0]
            val lastWholeArray = array[array.size - 1]

            if (firstWholeArray >= lastWholeArray) {
                println("Not in order")
            } else {
                for (i in array.size - 1 downTo 0) {
                    val lastItem = array[i]
                    if (i != 0) {
                        val previousItem = array[i - 1]
                        println("Last item: $lastItem, Previous item: $previousItem")

                        if (lastItem < previousItem) {
                            indexToCorrectOrder = i - 1

                            println("--- Need to correct order at index: $indexToCorrectOrder --- ")
                            if (indexToCorrectOrder == 0) {
                                println("--- No need to correct order --- ")
                                break
                            }
                            val itemToDiffTo = array.getOrNull(indexToCorrectOrder) ?: continue
                            val intBounds = (array[i - 2] + 1) until lastItem
                            println("Bounds to check: $intBounds")
                            for (j in array.size - 1 downTo 0) {
                                val itemToDiffFrom = array[j]
                                val diff = itemToDiffTo - itemToDiffFrom
                                println("Diff: $diff, from: $itemToDiffFrom, to: $itemToDiffTo")
                                if (diff in intBounds) {
                                    array[indexToCorrectOrder] = diff
                                }
                            }
                        }
                    }
                }
            }
        }

        println("\n--- Original array: ${og.joinToString(", ")} ---")
        println("--- Corrected array: ${array.joinToString(", ")} ---")
        assert(true)
    }


}