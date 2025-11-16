package com.chanssem.freedive.ui.table

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chanssem.freedive.R

@Composable
fun TimeInputDialog(
    currentMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val totalSeconds = (currentMillis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    var minutesText by remember { mutableStateOf(minutes.toString()) }
    var secondsText by remember { mutableStateOf(seconds.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.time_input_title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                minutesText = newValue
                            }
                        },
                        label = { Text(stringResource(R.string.minutes)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Text(":")
                    OutlinedTextField(
                        value = secondsText,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                secondsText = newValue
                            }
                        },
                        label = { Text(stringResource(R.string.seconds)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val min = minutesText.toIntOrNull() ?: 0
                    val sec = secondsText.toIntOrNull() ?: 0
                    val totalMillis = ((min * 60) + sec) * 1000L
                    if (totalMillis >= 15_000L) { // 최소 15초
                        onConfirm(totalMillis)
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

