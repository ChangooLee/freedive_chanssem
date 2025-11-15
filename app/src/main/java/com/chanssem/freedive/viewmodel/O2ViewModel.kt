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

class O2ViewModel : ViewModel() {

    private val _breathMillis = MutableStateFlow(120_000L) // 기본 2분
    val breathMillis: StateFlow<Long> = _breathMillis.asStateFlow()

    private val _targetHoldMillis = MutableStateFlow(180_000L) // 기본 3분
    val targetHoldMillis: StateFlow<Long> = _targetHoldMillis.asStateFlow()

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
        _rounds.value = TableGenerator.generateO2Table(
            roundCount = _rounds.value.size.takeIf { it > 0 } ?: 8,
            breathMillis = _breathMillis.value,
            targetHoldMillis = _targetHoldMillis.value
        )
    }

    fun changeBreathTime(delta: Long) {
        if (_isRunning.value) return
        val newValue = (_breathMillis.value + delta).coerceAtLeast(15_000L)
        _breathMillis.value = newValue
        updateRounds()
    }

    fun setBreathTime(millis: Long) {
        if (_isRunning.value) return
        val newValue = millis.coerceAtLeast(15_000L)
        _breathMillis.value = newValue
        updateRounds()
    }

    fun changeTargetHoldTime(delta: Long) {
        if (_isRunning.value) return
        val newValue = (_targetHoldMillis.value + delta).coerceAtLeast(15_000L)
        _targetHoldMillis.value = newValue
        updateRounds()
    }

    fun setTargetHoldTime(millis: Long) {
        if (_isRunning.value) return
        val newValue = millis.coerceAtLeast(15_000L)
        _targetHoldMillis.value = newValue
        updateRounds()
    }

    fun addRound() {
        if (_isRunning.value) return
        val currentCount = _rounds.value.size
        _rounds.value = TableGenerator.generateO2Table(
            roundCount = currentCount + 1,
            breathMillis = _breathMillis.value,
            targetHoldMillis = _targetHoldMillis.value
        )
    }

    fun removeRound(index: Int) {
        if (_isRunning.value) return
        val currentCount = _rounds.value.size
        if (currentCount <= 6) return
        _rounds.value = TableGenerator.generateO2Table(
            roundCount = currentCount - 1,
            breathMillis = _breathMillis.value,
            targetHoldMillis = _targetHoldMillis.value
        )
    }

    fun startSession(speak: (String) -> Unit) {
        if (_isRunning.value) return

        _isRunning.value = true
        _currentState.value = null

        sessionTimer = SessionTimer(viewModelScope)
        sessionTimer?.start(
            tableType = TableType.O2,
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

