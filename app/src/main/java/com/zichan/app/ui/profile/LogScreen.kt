package com.zichan.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.StatusGreen
import com.zichan.app.ui.theme.StatusRed
import com.zichan.app.ui.theme.StatusYellow
import com.zichan.app.ui.theme.TextSecondary
import com.zichan.app.ui.theme.TextTertiary
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogScreen(
    onBack: () -> Unit = {},
    viewModel: LogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingClear by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    if (pendingClear) {
        AlertDialog(
            onDismissRequest = { pendingClear = false },
            title = { Text("确认清空") },
            text = { Text("确定要删除所有操作日志吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAllLogs(); pendingClear = false }) {
                    Text("清空", color = StatusRed)
                }
            },
            dismissButton = { TextButton(onClick = { pendingClear = false }) { Text("取消") } }
        )
    }

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(
            title = "操作日志",
            onBack = onBack,
            actions = {
                if (state.logs.isNotEmpty()) {
                    IconButton(onClick = { pendingClear = true }) {
                        Icon(Icons.Filled.DeleteForever, "清空", tint = StatusRed, modifier = Modifier.size(22.dp))
                    }
                    IconButton(onClick = { viewModel.exportAndShare() }) {
                        Icon(Icons.Filled.Share, "导出分享", tint = Amber500, modifier = Modifier.size(22.dp))
                    }
                }
            }
        )
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Amber500)
            }
        } else if (state.logs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无记录", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.logs) { log ->
                    ZichanCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(opColor(log.operation).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(log.operation, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = opColor(log.operation))
                            }
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(log.detail, style = MaterialTheme.typography.bodySmall,
                                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight)
                                Spacer(Modifier.height(2.dp))
                                Text(dateFormat.format(Date(log.timestamp)),
                                    style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            }
                            val confirming = pendingDeleteId == log.id
                            IconButton(
                                onClick = {
                                    if (confirming) {
                                        viewModel.deleteLog(log)
                                        pendingDeleteId = null
                                    } else {
                                        pendingDeleteId = log.id
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Delete, "删除", Modifier.size(16.dp),
                                    tint = if (confirming) StatusRed else TextTertiary
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

private fun opColor(op: String) = when (op) {
    "添加" -> StatusGreen
    "修改" -> Amber500
    "删除" -> StatusRed
    "出售" -> StatusYellow
    "丢弃" -> StatusRed
    "借出" -> Amber500
    "归还" -> StatusGreen
    else -> TextSecondary
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
