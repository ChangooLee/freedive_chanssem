package com.chanssem.freedive.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chanssem.freedive.R

@Composable
fun ChanssemScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.chanssem_logo),
            contentDescription = "Chanssem Logo",
            modifier = Modifier.size(150.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "🧑‍🏫 찬쌤 소개",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "PADI 프리다이빙 강사 트레이너이자 수중 촬영가 Chanssem(이찬구)이 만든 프리다이빙 트레이닝 앱입니다.\n\n" +
                    "안전하고 체계적인 CO₂ / O₂ / 원브레스 훈련을 통해, 더 오래·더 편안하게 숨을 참을 수 있도록 돕고자 합니다.\n\n" +
                    "프리다이빙 강습과 투어, 수중 촬영, 그리고 최신 소식은 인스타그램 @chanssem 에서 확인하실 수 있습니다.",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            "🌊 MOBA(Make Ocean Blue Again) 소개",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Image(
            painter = painterResource(id = R.drawable.moba_logo),
            contentDescription = "MOBA Logo",
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "MOBA(Make Ocean Blue Again)는 프리다이빙과 플로깅, 환경 캠페인을 통해 바다와 물을 지키는 행동을 이어가는 해양 보전 프로젝트입니다.\n\n" +
                    "기업과 다이버, 시민이 함께 참여하는 ESG 플로깅과 해양 정화 활동, 교육 프로그램을 통해 \"바다를 다시 푸르게\" 만들고자 합니다.\n\n" +
                    "MOBA에 대한 더 자세한 소개와 활동 내용은 https://moba-project.org 에서 확인하실 수 있습니다.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

