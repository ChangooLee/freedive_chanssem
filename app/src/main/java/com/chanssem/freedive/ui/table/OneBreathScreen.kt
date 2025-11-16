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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chanssem.freedive.R
import com.chanssem.freedive.model.SessionPhase
import com.chanssem.freedive.viewmodel.OneBreathViewModel

@Composable
fun OneBreathScreen(
    speak: (String) -> Unit,
    viewModel: OneBreathViewModel = viewModel()
) {
    val holdMillis by viewModel.holdMillis.collectAsState()
    val oneBreathMillis by viewModel.oneBreathMillis.collectAsState()
    val rounds by viewModel.rounds.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val currentState by viewModel.currentState.collectAsState()

    var showHoldTimeDialog by remember { mutableStateOf(false) }
    var showOneBreathTimeDialog by remember { mutableStateOf(false) }

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
                        text = stringResource(R.string.round_progress, currentState!!.currentRoundIndex + 1, currentState!!.totalRounds),
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
                                text = stringResource(R.string.one_breath_label),
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
                                    currentRound?.let { TimeFormatter.formatSeconds((it.breathMillis / 1000).toInt()) } ?: "0초"
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
                                text = stringResource(R.string.hold),
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
            // 상단: Hold 시간과 One-breath 시간 설정 (좌우 배치)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                    // 좌측: 원브레스 시간
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.one_breath_time),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { viewModel.changeOneBreathTime(-1_000L) },
                                enabled = oneBreathMillis > 3_000L,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text(
                                    text = "−",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = TimeFormatter.formatSeconds((oneBreathMillis / 1000).toInt()),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        showOneBreathTimeDialog = true
                                    }
                                    .padding(horizontal = 12.dp)
                            )
                            IconButton(
                                onClick = { viewModel.changeOneBreathTime(1_000L) },
                                enabled = oneBreathMillis < 10_000L,
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
                    }
                    
                    // 구분선
                    VerticalDivider(modifier = Modifier.height(60.dp))
                    
                    // 우측: 숨참기 시간
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.hold_time),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
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
                                        showHoldTimeDialog = true
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
                    }
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
                            .height(48.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R${index + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(0.2f)
                        )
                        Text(
                            text = "One-breath: ${TimeFormatter.formatSeconds((round.breathMillis / 1000).toInt())}",
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
                                enabled = !isRunning,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-")
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
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
            enabled = !isRunning && rounds.size < 12
        ) {
            Text(stringResource(R.string.add_round))
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
            Text(if (isRunning) stringResource(R.string.stop) else stringResource(R.string.start))
        }
    }

    // 시간 입력 다이얼로그
    if (showHoldTimeDialog) {
        TimeInputDialog(
            currentMillis = holdMillis,
            onDismiss = { showHoldTimeDialog = false },
            onConfirm = { newMillis ->
                viewModel.setHoldTime(newMillis)
                showHoldTimeDialog = false
            }
        )
    }

    if (showOneBreathTimeDialog) {
        // One-breath는 초 단위만 입력
        var secondsText by remember { mutableStateOf((oneBreathMillis / 1000).toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showOneBreathTimeDialog = false },
            title = { Text("원브레스 시간 설정") },
            text = {
                OutlinedTextField(
                    value = secondsText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            secondsText = newValue
                        }
                    },
                    label = { Text("초 (3~10)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val sec = secondsText.toIntOrNull() ?: 6
                        val totalMillis = sec.coerceIn(3, 10) * 1000L
                        viewModel.setOneBreathTime(totalMillis)
                        showOneBreathTimeDialog = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOneBreathTimeDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

