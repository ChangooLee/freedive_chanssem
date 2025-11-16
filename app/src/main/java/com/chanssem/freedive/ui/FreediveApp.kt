package com.chanssem.freedive.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
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
import com.chanssem.freedive.utils.LanguageManager
import com.chanssem.freedive.viewmodel.Co2ViewModel
import com.chanssem.freedive.viewmodel.O2ViewModel
import com.chanssem.freedive.viewmodel.OneBreathViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreediveApp(
    speak: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAbout by remember { mutableStateOf(false) }
    var showTabChangeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var pendingTabIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    
    // 현재 언어 상태
    val currentLanguage = remember {
        LanguageManager.getSavedLanguage(context)
    }
    
    // 탭 이름을 리소스에서 가져오기
    val tabs = listOf(
        stringResource(R.string.tab_co2),
        stringResource(R.string.tab_o2),
        stringResource(R.string.tab_one_breath)
    )
    
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
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = stringResource(R.string.select_language)
                        )
                    }
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
                    title = { Text(stringResource(R.string.session_stop_title)) },
                    text = { Text(stringResource(R.string.session_stop_message)) },
                    confirmButton = {
                        TextButton(onClick = { confirmTabChange() }) {
                            Text(stringResource(R.string.stop_session))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { 
                            showTabChangeDialog = false
                            pendingTabIndex = null
                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
            
            // 언어 선택 다이얼로그
            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text(stringResource(R.string.select_language)) },
                    text = {
                        Column {
                            LanguageManager.Language.values().forEach { language ->
                                val languageName = when (language) {
                                    LanguageManager.Language.KOREAN -> stringResource(R.string.language_korean)
                                    LanguageManager.Language.ENGLISH -> stringResource(R.string.language_english)
                                    LanguageManager.Language.JAPANESE -> stringResource(R.string.language_japanese)
                                    LanguageManager.Language.CHINESE -> stringResource(R.string.language_chinese)
                                }
                                val isSelected = currentLanguage == language
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            LanguageManager.saveLanguage(context, language)
                                            showLanguageDialog = false
                                            // 앱 재시작
                                            (context as? Activity)?.recreate()
                                        }
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = languageName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showLanguageDialog = false }) {
                            Text(stringResource(R.string.close))
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
                                stringResource(R.string.about_title),
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
                                stringResource(R.string.chanssem_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(R.string.chanssem_description),
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
                                stringResource(R.string.moba_title),
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
                                stringResource(R.string.moba_description),
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
                                Text(stringResource(R.string.close))
                            }
                        }
                    }
                }
            }
        }
    }
}

