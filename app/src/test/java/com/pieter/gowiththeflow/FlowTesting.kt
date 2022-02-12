package com.pieter.gowiththeflow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowTesting {

    @Test
    fun mockFlowTest() {
        runBlocking {
            assertEquals(EXPECTED, mockFlow().first())
        }
    }

    private fun mockFlow(): Flow<String> {
        return flow {
            emit("mocked")
        }
    }

    @Test
    fun mockMultipleFlowTest() {
        runBlocking {
            assertEquals(EXPECTED_LIST, mockMultipleFlow().toList())
        }
    }

    private fun mockMultipleFlow(): Flow<String> {
        return flow {
            emit("1")
            emit("2")
            emit("3")
        }
    }

    companion object {
        const val EXPECTED = "mocked"
        val EXPECTED_LIST = arrayListOf("1", "2", "3")
    }

}