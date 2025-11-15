package com.chanssem.freedive.timer

import com.chanssem.freedive.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SessionTimer(
    private val scope: CoroutineScope
) {

    data class Callbacks(
        val onTick: (SessionState) -> Unit,
        val onTts: (String) -> Unit,
        val onCompleted: () -> Unit
    )

    private var job: kotlinx.coroutines.Job? = null

    fun start(
        tableType: TableType,
        rounds: List<Round>,
        callbacks: Callbacks
    ) {
        stop()

        job = scope.launch {
            val totalRounds = rounds.size
            for ((roundIndex, round) in rounds.withIndex()) {
                if (!isActive) break

                when (tableType) {
                    TableType.CO2,
                    TableType.O2 -> {
                        // Breath -> Hold
                        runPhase(
                            tableType = tableType,
                            roundIndex = roundIndex,
                            totalRounds = totalRounds,
                            phase = SessionPhase.BREATH,
                            durationMillis = round.breathMillis,
                            callbacks = callbacks
                        )
                        if (!isActive) break
                        runPhase(
                            tableType = tableType,
                            roundIndex = roundIndex,
                            totalRounds = totalRounds,
                            phase = SessionPhase.HOLD,
                            durationMillis = round.holdMillis,
                            callbacks = callbacks
                        )
                    }
                    TableType.ONE_BREATH -> {
                        // Hold -> Breath(one-breath)
                        runPhase(
                            tableType = tableType,
                            roundIndex = roundIndex,
                            totalRounds = totalRounds,
                            phase = SessionPhase.HOLD,
                            durationMillis = round.holdMillis,
                            callbacks = callbacks
                        )
                        if (!isActive) break
                        runPhase(
                            tableType = tableType,
                            roundIndex = roundIndex,
                            totalRounds = totalRounds,
                            phase = SessionPhase.BREATH,
                            durationMillis = round.breathMillis,
                            callbacks = callbacks
                        )
                    }
                }
            }

            if (isActive) {
                callbacks.onCompleted()
            }
        }
    }

    private suspend fun runPhase(
        tableType: TableType,
        roundIndex: Int,
        totalRounds: Int,
        phase: SessionPhase,
        durationMillis: Long,
        callbacks: Callbacks
    ) {
        var remaining = durationMillis

        // phase 시작 TTS
        when (phase) {
            SessionPhase.BREATH -> {
                callbacks.onTts("Breath")
            }
            SessionPhase.HOLD -> {
                callbacks.onTts("Hold")
            }
        }

        while (remaining > 0) {
            val state = SessionState(
                tableType = tableType,
                currentRoundIndex = roundIndex,
                totalRounds = totalRounds,
                phase = phase,
                remainingMillis = remaining,
                isRunning = true
            )
            callbacks.onTick(state)

            val remainingSeconds = (remaining / 1000).toInt()
            if (remainingSeconds == 15) {
                when (phase) {
                    SessionPhase.BREATH -> callbacks.onTts("15 seconds, ready")
                    SessionPhase.HOLD -> callbacks.onTts("15 seconds remain")
                }
            }
            if (remainingSeconds in 1..10) {
                callbacks.onTts(remainingSeconds.toString())
            }

            delay(1000)
            remaining -= 1000
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}

