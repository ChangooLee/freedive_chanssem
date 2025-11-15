package com.chanssem.freedive.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Intent
import android.net.Uri
import com.chanssem.freedive.R
import com.chanssem.freedive.ui.table.Co2TableScreen
import com.chanssem.freedive.ui.table.O2TableScreen
import com.chanssem.freedive.ui.table.OneBreathScreen
import com.chanssem.freedive.viewmodel.Co2ViewModel
import com.chanssem.freedive.viewmodel.O2ViewModel
import com.chanssem.freedive.viewmodel.OneBreathViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreediveApp(
    speak: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAbout by remember { mutableStateOf(false) }
    var showTabChangeDialog by remember { mutableStateOf(false) }
    var pendingTabIndex by remember { mutableStateOf<Int?>(null) }
    val tabs = listOf("CO₂", "O₂", "One Breath")
    val context = LocalContext.current
    
    // 각 ViewModel 인스턴스 생성
    val co2ViewModel: Co2ViewModel = viewModel()
    val o2ViewModel: O2ViewModel = viewModel()
    val oneBreathViewModel: OneBreathViewModel = viewModel()
    
    // 각 ViewModel의 isRunning 상태 확인
    val co2IsRunning by co2ViewModel.isRunning.collectAsState()
    val o2IsRunning by o2ViewModel.isRunning.collectAsState()
    val oneBreathIsRunning by oneBreathViewModel.isRunning.collectAsState()
    
    // 탭 변경 핸들러
    val handleTabChange: (Int) -> Unit = { newTabIndex ->
        // 현재 활성화된 ViewModel의 실행 상태 확인
        val currentViewModelIsRunning = when (selectedTab) {
            0 -> co2IsRunning
            1 -> o2IsRunning
            2 -> oneBreathIsRunning
            else -> false
        }
        
        if (currentViewModelIsRunning && newTabIndex != selectedTab) {
            pendingTabIndex = newTabIndex
            showTabChangeDialog = true
        } else {
            selectedTab = newTabIndex
        }
    }
    
    // 탭 변경 확인 후 실행
    val confirmTabChange: () -> Unit = {
        when (selectedTab) {
            0 -> co2ViewModel.stopSession()
            1 -> o2ViewModel.stopSession()
            2 -> oneBreathViewModel.stopSession()
        }
        pendingTabIndex?.let {
            selectedTab = it
        }
        pendingTabIndex = null
        showTabChangeDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Freedive Chanssem",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showAbout = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { handleTabChange(index) },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> Co2TableScreen(viewModel = co2ViewModel, speak = speak)
                1 -> O2TableScreen(viewModel = o2ViewModel, speak = speak)
                2 -> OneBreathScreen(viewModel = oneBreathViewModel, speak = speak)
            }
            
            // 탭 변경 확인 다이얼로그
            if (showTabChangeDialog) {
                AlertDialog(
                    onDismissRequest = { 
                        showTabChangeDialog = false
                        pendingTabIndex = null
                    },
                    title = { Text("세션 중단") },
                    text = { Text("현재 세션이 진행 중입니다. 세션을 중단하고 다른 탭으로 이동하시겠습니까?") },
                    confirmButton = {
                        TextButton(onClick = { confirmTabChange() }) {
                            Text("중단")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { 
                            showTabChangeDialog = false
                            pendingTabIndex = null
                        }) {
                            Text("취소")
                        }
                    }
                )
            }
        }

        if (showAbout) {
            Dialog(onDismissRequest = { showAbout = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 제목 바
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "About freedive chanssem & MOBA",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showAbout = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }
                        }
                        
                        HorizontalDivider()
                        
                        // 스크롤 가능한 내용
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🧑‍🏫 찬쌤 소개",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "PADI 프리다이빙 강사 트레이너이자 수중 촬영가 Chanssem(이찬구)이 만든 프리다이빙 트레이닝 앱입니다.\n\n" +
                                        "안전하고 체계적인 CO₂ / O₂ / 원브레스 훈련을 통해, 더 오래·더 편안하게 숨을 참을 수 있도록 돕고자 합니다.\n\n" +
                                        "프리다이빙 강습과 투어, 수중 촬영, 그리고 최신 소식은 인스타그램에서 확인하실 수 있습니다.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/chanssem"))
                                    context.startActivity(intent)
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.instagram_icon),
                                    contentDescription = "Instagram",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "@chanssem",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "🌊 MOBA(Make Ocean Blue Again) 소개",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.moba_logo),
                                contentDescription = "MOBA Logo",
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "MOBA(Make Ocean Blue Again)는 프리다이빙과 플로깅, 환경 캠페인을 통해 바다와 물을 지키는 행동을 이어가는 해양 보전 프로젝트입니다.\n\n" +
                                        "기업과 다이버, 시민이 함께 참여하는 ESG 플로깅과 해양 정화 활동, 교육 프로그램을 통해 \"바다를 다시 푸르게\" 만들고자 합니다.\n\n" +
                                        "MOBA에 대한 더 자세한 소개와 활동 내용은 아래 링크에서 확인하실 수 있습니다.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "https://moba-project.org",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://moba-project.org"))
                                    context.startActivity(intent)
                                }
                            )
                        }
                        
                        HorizontalDivider()
                        
                        // 하단 버튼
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAbout = false }) {
                                Text("닫기")
                            }
                        }
                    }
                }
            }
        }
    }
}

