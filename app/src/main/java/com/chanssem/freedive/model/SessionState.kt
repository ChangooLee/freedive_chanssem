package com.chanssem.freedive.model

data class SessionState(
    val tableType: TableType,
    val currentRoundIndex: Int,  // 0-based index
    val totalRounds: Int,
    val phase: SessionPhase,
    val remainingMillis: Long,
    val isRunning: Boolean
)

