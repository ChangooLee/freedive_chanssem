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

class OneBreathViewModel : ViewModel() {

    private val _holdMillis = MutableStateFlow(60_000L) // 기본 1분
    val holdMillis: StateFlow<Long> = _holdMillis.asStateFlow()

    private val _oneBreathMillis = MutableStateFlow(6_000L) // 기본 6초
    val oneBreathMillis: StateFlow<Long> = _oneBreathMillis.asStateFlow()

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
        _rounds.value = TableGenerator.generateOneBreathTable(
            roundCount = _rounds.value.size.takeIf { it > 0 } ?: 8,
            holdMillis = _holdMillis.value,
            oneBreathMillis = _oneBreathMillis.value
        )
    }

    fun changeHoldTime(delta: Long) {
        if (_isRunning.value) return
        val newValue = (_holdMillis.value + delta).coerceAtLeast(15_000L)
        _holdMillis.value = newValue
        updateRounds()
    }

    fun setHoldTime(millis: Long) {
        if (_isRunning.value) return
        val newValue = millis.coerceAtLeast(15_000L)
        _holdMillis.value = newValue
        updateRounds()
    }

    fun changeOneBreathTime(delta: Long) {
        if (_isRunning.value) return
        val newValue = (_oneBreathMillis.value + delta).coerceIn(3_000L, 10_000L)
        _oneBreathMillis.value = newValue
        updateRounds()
    }

    fun setOneBreathTime(millis: Long) {
        if (_isRunning.value) return
        val newValue = millis.coerceIn(3_000L, 10_000L)
        _oneBreathMillis.value = newValue
        updateRounds()
    }

    fun addRound() {
        if (_isRunning.value) return
        val currentCount = _rounds.value.size
        if (currentCount >= 12) return // 최대 12라운드
        _rounds.value = TableGenerator.generateOneBreathTable(
            roundCount = currentCount + 1,
            holdMillis = _holdMillis.value,
            oneBreathMillis = _oneBreathMillis.value
        )
    }

    fun removeRound(index: Int) {
        if (_isRunning.value) return
        val currentCount = _rounds.value.size
        if (currentCount <= 6) return // 최소 6라운드 보장
        _rounds.value = TableGenerator.generateOneBreathTable(
            roundCount = currentCount - 1,
            holdMillis = _holdMillis.value,
            oneBreathMillis = _oneBreathMillis.value
        )
    }

    fun startSession(speak: (String) -> Unit) {
        if (_isRunning.value) return

        _isRunning.value = true
        _currentState.value = null

        sessionTimer = SessionTimer(viewModelScope)
        sessionTimer?.start(
            tableType = TableType.ONE_BREATH,
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

