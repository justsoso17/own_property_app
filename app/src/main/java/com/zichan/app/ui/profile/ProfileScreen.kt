package com.zichan.app.ui.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.StatusRed
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar

@Composable
fun ProfileScreen(
    onPersonList: () -> Unit = {},
    onLogView: () -> Unit = {},
    viewModel: ProfileViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var importUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }

    if (showImportDialog && importUri != null) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false; importUri = null },
            title = { Text("导入方式") },
            text = { Text("合并：保留现有数据，跳过重复资产\n替换：删除现有数据，用备份覆盖") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.importData(importUri!!, replace = true)
                    showImportDialog = false; importUri = null
                }) { Text("替换", color = StatusRed) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        viewModel.importData(importUri!!, replace = false)
                        showImportDialog = false; importUri = null
                    }) { Text("合并", color = Amber500) }
                    TextButton(onClick = { showImportDialog = false; importUri = null }) { Text("取消") }
                }
            }
        )
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { importUri = it; showImportDialog = true }
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(title = "我的")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ZichanCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Filled.OpenInBrowser,
                        title = "项目仓库",
                        subtitle = "查看源代码与新版本",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/justsoso17/own_property_app"))
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 56.dp))
                    SettingsRow(
                        icon = Icons.Filled.People,
                        title = "联系人管理",
                        subtitle = "管理借还联系人",
                        onClick = onPersonList
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 56.dp))
                    SettingsRow(
                        icon = Icons.Filled.Description,
                        title = "操作日志",
                        subtitle = "查看资产变更记录",
                        onClick = onLogView
                    )
                }
            }

            ZichanCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Filled.FileDownload,
                        title = "导出备份",
                        subtitle = "导出所有数据到下载目录",
                        onClick = { viewModel.exportData() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 56.dp))
                    SettingsRow(
                        icon = Icons.Filled.Backup,
                        title = "导入备份",
                        subtitle = "从 JSON 备份文件恢复数据",
                        onClick = { importLauncher.launch(arrayOf("application/json")) }
                    )
                }
            }

            ZichanCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Filled.OpenInBrowser,
                        title = "不要点击这里",
                        subtitle = "点了你会后悔的",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/android_backup322"))
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 56.dp))
                    SettingsRow(
                        icon = Icons.Filled.OpenInBrowser,
                        title = "这个也不要点",
                        subtitle = "什么都没有真的",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://baike.baidu.com/video?collectionId=0&from=lemma&fromIndex=0&fromModule=lemma_top&fromPage=lemmaTop&isSensitive=0&lemmaId=1719254&secondId=92970715"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "资产管家 v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = trailing == null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Amber500, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null,
                modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
