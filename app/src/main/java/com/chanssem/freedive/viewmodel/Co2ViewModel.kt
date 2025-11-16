package com.chanssem.freedive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chanssem.freedive.domain.TableGenerator
import com.chanssem.freedive.model.Round
import com.chanssem.freedive.model.SessionState
import com.chanssem.freedive.model.TableType
import com.chanssem.freedive.timer.SessionTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Co2ViewModel : ViewModel() {

    private val _minBreathMillis = MutableStateFlow(15_000L) // 기본 15초
    val minBreathMillis: StateFlow<Long> = _minBreathMillis.asStateFlow()

    private val _holdMillis = MutableStateFlow(60_000L) // 기본 1분
    val holdMillis: StateFlow<Long> = _holdMillis.asStateFlow()

    private val _rounds = MutableStateFlow<List<Round>>(emptyList())
    val rounds: StateFlow<List<Round>> = _rounds.asStateFlow()

    private val _currentState = MutableStateFlow<SessionState?>(null)
    val currentState: StateFlow<SessionState?> = _currentState.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var sessionTimer: SessionTimer? = null

    init {
        updateRounds()
    }

    private fun updateRounds() {
        _rounds.value = TableGenerator.generateCo2Table(
            roundCount = _rounds.value.size.takeIf { it > 0 } ?: 8,
            minBreathMillis = _minBreathMillis.value,
            holdMillis = _holdMillis.value
        )
    }

    fun changeMinBreathTime(delta: Long) {
        if (_isRunning.value) return
        val newValue = (_minBreathMillis.value + delta).coerceIn(10_000L, 60_000L) // 최소 10초, 최대 60초
        _minBreathMillis.value = newValue
        updateRounds()
    }

    fun setMinBreathTime(millis: Long) {
        if (_isRunning.value) return
        val newValue = millis.coerceIn(10_000L, 60_000L) // 최소 10초, 최대 60초
        _minBreathMillis.value = newValue
        updateRounds()
    }

    fun changeHoldTime(delta: Long) {
        if (_isRunning.value) return
        val newValue = (_holdMillis.value + delta).coerceAtLeast(15_000L) // 최소 15초
        _holdMillis.value = newValue
        updateRounds()
    }

    fun setHoldTime(millis: Long) {
        if (_isRunning.value) return
        val newValue = millis.coerceAtLeast(15_000L) // 최소 15초
        _holdMillis.value = newValue
        updateRounds()
    }

    fun addRound() {
        if (_isRunning.value) return
        val currentCount = _rounds.value.size
        _rounds.value = TableGenerator.generateCo2Table(
            roundCount = currentCount + 1,
            minBreathMillis = _minBreathMillis.value,
            holdMillis = _holdMillis.value
        )
    }

    fun removeRound(index: Int) {
        if (_isRunning.value) return
        val currentCount = _rounds.value.size
        if (currentCount <= 6) return // 최소 6라운드 보장
        _rounds.value = TableGenerator.generateCo2Table(
            roundCount = currentCount - 1,
            minBreathMillis = _minBreathMillis.value,
            holdMillis = _holdMillis.value
        )
    }

    fun startSession(speak: (String) -> Unit) {
        if (_isRunning.value) return

        _isRunning.value = true
        _currentState.value = null

        sessionTimer = SessionTimer(viewModelScope)
        sessionTimer?.start(
            tableType = TableType.CO2,
            rounds = _rounds.value,
            callbacks = SessionTimer.Callbacks(
                onTick = { state ->
                    _currentState.value = state
                },
                onTts = { text ->
                    speak(text)
                },
                onCompleted = {
                    _isRunning.value = false
                    _currentState.value = null
                    speak("Session complete")
                }
            )
        )
    }

    fun stopSession() {
        sessionTimer?.stop()
        _isRunning.value = false
        _currentState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopSession()
    }
}

