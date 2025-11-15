package com.chanssem.freedive.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chanssem.freedive.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashEnd: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1000) // 1초 후 메인 화면으로
        onSplashEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.chanssem_logo),
            contentDescription = "Chanssem Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}

