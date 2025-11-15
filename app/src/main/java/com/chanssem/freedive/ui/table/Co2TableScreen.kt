package com.chanssem.freedive.ui.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chanssem.freedive.model.SessionPhase
import com.chanssem.freedive.viewmodel.Co2ViewModel

@Composable
fun Co2TableScreen(
    speak: (String) -> Unit,
    viewModel: Co2ViewModel = viewModel()
) {
    val holdMillis by viewModel.holdMillis.collectAsState()
    val rounds by viewModel.rounds.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val currentState by viewModel.currentState.collectAsState()

    var showTimeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단: 세션 진행 중이면 세션 상태, 아니면 시간 설정
        if (isRunning && currentState != null) {
            // 세션 진행 중 상태 표시
            val currentRound = rounds.getOrNull(currentState!!.currentRoundIndex)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Round ${currentState!!.currentRoundIndex + 1} / ${currentState!!.totalRounds}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Breath",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (currentState!!.phase == SessionPhase.BREATH) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                            Text(
                                text = if (currentState!!.phase == SessionPhase.BREATH) {
                                    TimeFormatter.formatMillis(currentState!!.remainingMillis)
                                } else {
                                    currentRound?.let { TimeFormatter.formatMillis(it.breathMillis) } ?: "00:00"
                                },
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (currentState!!.phase == SessionPhase.BREATH) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                        }
                        VerticalDivider(modifier = Modifier.height(60.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Hold",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (currentState!!.phase == SessionPhase.HOLD) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                            Text(
                                text = if (currentState!!.phase == SessionPhase.HOLD) {
                                    TimeFormatter.formatMillis(currentState!!.remainingMillis)
                                } else {
                                    currentRound?.let { TimeFormatter.formatMillis(it.holdMillis) } ?: "00:00"
                                },
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (currentState!!.phase == SessionPhase.HOLD) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // 상단: Hold 시간 설정
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "숨참기 시간",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { viewModel.changeHoldTime(-15_000L) },
                            enabled = holdMillis > 15_000L,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text(
                                text = "−",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = TimeFormatter.formatMillis(holdMillis),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    showTimeDialog = true
                                }
                                .padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { viewModel.changeHoldTime(15_000L) },
                            enabled = true,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // 목표 STA 계산 표시
                    val targetStaMillis = (holdMillis / 0.7).toLong()
                    Text(
                        text = "목표 STA: ${TimeFormatter.formatMillis(targetStaMillis)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 라운드 테이블
        Card(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(rounds) { index, round ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R${index + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(0.2f)
                        )
                        Text(
                            text = "Breath: ${TimeFormatter.formatMillis(round.breathMillis)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = "Hold: ${TimeFormatter.formatMillis(round.holdMillis)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(0.3f)
                        )
                        if (index + 1 >= 7 && rounds.size > 6) {
                            IconButton(
                                onClick = { viewModel.removeRound(index) },
                                enabled = !isRunning
                            ) {
                                Text("-")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 하단 버튼들
        Button(
            onClick = { viewModel.addRound() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRunning
        ) {
            Text("라운드 추가 (+)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isRunning) {
                    viewModel.stopSession()
                } else {
                    viewModel.startSession(speak)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) androidx.compose.ui.graphics.Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isRunning) "STOP" else "START")
        }
    }

    // 시간 입력 다이얼로그
    if (showTimeDialog) {
        TimeInputDialog(
            currentMillis = holdMillis,
            onDismiss = { showTimeDialog = false },
            onConfirm = { newMillis ->
                viewModel.setHoldTime(newMillis)
                showTimeDialog = false
            }
        )
    }
}

