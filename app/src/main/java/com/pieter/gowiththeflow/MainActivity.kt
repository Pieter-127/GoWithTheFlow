package com.pieter.gowiththeflow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = arrayListOf(0, 1, 1, 2, 3, 5)

        val flow: Flow<Int> = flow {
            data.forEach {
                emit(it)
                delay(500)
            }
        }

        val stateFlow = MutableStateFlow<ViewState>(ViewState.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            flow.distinctUntilChanged().map {
                Mapper(it).format()
            }.flowOn(Dispatchers.IO)
                .onEach {
                    Log.v("i_did_not_add_timber", it)
                }.catch { exception ->
                    Log.e("i_did_not_add_timber", exception.message ?: exception.localizedMessage)
                    stateFlow.value = ViewState.Failure()
                }.collect {
                    stateFlow.value = ViewState.Success()
                }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                stateFlow.distinctUntilChangedBy { stateFlow }.collect { state ->
                    when (state) {
                        is ViewState.Loading -> Log.v("i_did_not_add_timber", state.description)
                        is ViewState.Success -> Log.v("i_did_not_add_timber", state.description)
                        is ViewState.Failure -> Log.v("i_did_not_add_timber", state.description)
                    }
                }
            }
        }
    }
}

sealed class ViewState {
    class Loading : ViewState() {
        val description: String = "Loading"
    }

    class Success : ViewState() {
        val description: String = "Success"
    }

    class Failure : ViewState() {
        val description: String = "Failure"
    }
}

data class Mapper(private val content: Int) {
    fun format(): String = if (content > 1) "$content days" else "$content day"
}