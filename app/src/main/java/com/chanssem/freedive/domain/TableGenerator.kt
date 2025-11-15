package com.chanssem.freedive.domain

import com.chanssem.freedive.model.Round

object TableGenerator {

    /**
     * CO₂ 테이블
     * - 마지막 라운드 Breath = 15초
     * - 위로 올라갈수록 15초씩 증가
     * - Hold = 상단에서 설정한 holdMillis 고정 (기본 60초)
     */
    fun generateCo2Table(
        roundCount: Int,
        holdMillis: Long
    ): List<Round> {
        return (0 until roundCount).map { index ->
            // 마지막 라운드(index = roundCount - 1)는 15초
            // 위로 올라갈수록 15초씩 증가
            // breathSeconds = (roundCount - index) * 15
            val breathSeconds = (roundCount - index) * 15
            Round(
                breathMillis = breathSeconds * 1000L,
                holdMillis = holdMillis
            )
        }
    }

    /**
     * O₂ 테이블
     * - Breath = 상단에서 설정(기본 2분 = 120초, 15초 단위 증감)
     * - Hold = 목표 시간의 50% → 60% → ... → 90~100% 비율로 라운드별 증가
     * - targetHoldMillis = 상단에서 설정한 "목표 숨참기 시간"
     */
    fun generateO2Table(
        roundCount: Int,
        breathMillis: Long,
        targetHoldMillis: Long
    ): List<Round> {
        // 라운드별 비율 계산
        // 예: roundCount = 8이면 50, 60, 70, 80, 85, 90, 95, 100%
        val percentages = when (roundCount) {
            6 -> listOf(50, 60, 70, 80, 90, 100)
            7 -> listOf(50, 60, 70, 80, 85, 90, 100)
            8 -> listOf(50, 60, 70, 80, 85, 90, 95, 100)
            9 -> listOf(50, 55, 60, 70, 75, 80, 85, 90, 100)
            10 -> listOf(50, 55, 60, 65, 70, 75, 80, 85, 90, 100)
            else -> {
                // 기본적으로 균등 분배
                val step = 50.0 / (roundCount - 1)
                (0 until roundCount).map { (50 + step * it).toInt().coerceAtMost(100) }
            }
        }

        return percentages.map { percentage ->
            Round(
                breathMillis = breathMillis,
                holdMillis = (targetHoldMillis * percentage / 100)
            )
        }
    }

    /**
     * One-breath 테이블
     * - Hold = 모든 라운드 동일 (holdMillis)
     * - One-breath 시간 = 모든 라운드 동일 (oneBreathMillis)
     * - 라운드 수: 기본 8, 최소 6, 최대 12 (ViewModel에서 보장)
     * - 타이머 플로우는 Hold → One-breath 회복 → 다음 Hold
     */
    fun generateOneBreathTable(
        roundCount: Int,
        holdMillis: Long,
        oneBreathMillis: Long
    ): List<Round> {
        // breathMillis = oneBreathMillis, holdMillis = holdMillis로 모든 Round 동일
        return List(roundCount) {
            Round(
                breathMillis = oneBreathMillis,
                holdMillis = holdMillis
            )
        }
    }
}

