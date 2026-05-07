package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import com.zichan.app.ui.util.ZichanButton
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Orange500
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    assetId: Long,
    onEdit: (Long) -> Unit = {},
    onLend: (Long) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSellDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.reloadList()
    }

    val asset = state.assets.find { it.id == assetId }

    if (showDeleteDialog && asset != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除「${asset.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAsset(asset)
                    showDeleteDialog = false
                    onBack()
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
    }

    if (showSellDialog && asset != null) {
        AlertDialog(
            onDismissRequest = { showSellDialog = false },
            title = { Text("确认出售") },
            text = { Text("确定要将「${asset.name}」标记为已出售吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.sellAsset(asset)
                    showSellDialog = false
                }) { Text("确认") }
            },
            dismissButton = {
                TextButton(onClick = { showSellDialog = false }) { Text("取消") }
            }
        )
    }

    if (showDiscardDialog && asset != null) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("确认丢弃") },
            text = { Text("确定要将「${asset.name}」标记为已丢弃吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.discardAsset(asset)
                    showDiscardDialog = false
                }) { Text("确认") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("取消") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(
            title = asset?.name ?: "",
            onBack = onBack,
            actions = {
                if (asset != null) {
                    IconButton(onClick = { onEdit(asset.id) }) {
                        Icon(Icons.Filled.Edit, "编辑")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, "删除")
                    }
                }
            }
        )
        if (asset == null) {
            Column(Modifier.fillMaxSize()) {
                Text("资产不存在")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ZichanCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Orange500)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("购入价格", color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp)
                        Text(
                            NumberFormat.getCurrencyInstance(Locale.CHINA).format(asset.price),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                ZichanCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        DetailRow("状态", asset.status)
                        if (asset.brand.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("品牌", asset.brand)
                        }
                        if (asset.model.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("型号", asset.model)
                        }
                        if (asset.specs.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("规格", asset.specs)
                        }
                        if (asset.serialNumber.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("序列号", asset.serialNumber)
                        }
                        if (asset.purchaseChannel.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("购买渠道", asset.purchaseChannel)
                        }
                        if (asset.purchaseDate != null) {
                            HorizontalDivider()
                            DetailRow("购买日期", dateFormat.format(Date(asset.purchaseDate)))
                        }
                        if (asset.notes.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("备注", asset.notes)
                        }
                    }
                }

                if (asset.status == "使用中" || asset.status == "闲置") {
                    ZichanButton(
                        onClick = { onLend(asset.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange500)
                    ) { Text("借出") }
                }

                if (asset.status != "已出售" && asset.status != "已丢弃") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ZichanButton(
                            onClick = { showSellDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { Text("出售", color = MaterialTheme.colorScheme.onSurface) }
                        ZichanButton(
                            onClick = { showDiscardDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { Text("丢弃", color = MaterialTheme.colorScheme.onSurface) }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
