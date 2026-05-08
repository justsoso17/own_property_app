package com.zichan.app.ui.asset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.StatusIdle
import com.zichan.app.ui.theme.StatusRed
import com.zichan.app.ui.theme.TextSecondary
import com.zichan.app.ui.theme.StatusInUse
import com.zichan.app.ui.theme.StatusLent
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanFAB
import com.zichan.app.ui.util.ZichanTopBar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onAssetClick: (Long) -> Unit = {},
    onAddAsset: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var searchActive by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除选中的 ${selectedIds.size} 件资产吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAssets(selectedIds)
                    selectedIds = emptySet()
                    showDeleteDialog = false
                }) { Text("删除", color = StatusRed) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("取消") } }
        )
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            if (selectedIds.isNotEmpty()) {
                // Selection bar
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedIds = emptySet() }) {
                        Icon(Icons.Filled.Close, "取消", Modifier.size(22.dp))
                    }
                    Text("已选 ${selectedIds.size} 件", Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, "删除", Modifier.size(22.dp), tint = StatusRed)
                    }
                }
            } else {
                ZichanTopBar(
                    title = "资产",
                    actions = {
                        Icon(Icons.Filled.Search, "搜索",
                            modifier = Modifier.clickable { searchActive = true },
                            tint = MaterialTheme.colorScheme.onSurface)
                    }
                )
            }
            if (searchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.searchKeyword,
                        onValueChange = { viewModel.search(it) },
                        placeholder = { Text("搜索名称、品牌、型号...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Amber500.copy(alpha = 0.5f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = Amber500,
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.Search, null, Modifier.size(20.dp), tint = TextSecondary)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.search("")
                                searchActive = false
                            }) {
                                Icon(Icons.Filled.Close, "关闭", Modifier.size(20.dp))
                            }
                        }
                    )
                }
            }

            // Filter chips
            FilterChipRow(
                filterStatuses = state.filterStatuses,
                onToggleStatus = { viewModel.toggleFilterStatus(it) },
                onClear = {
                    viewModel.search("")
                    viewModel.clearFilters()
                }
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Amber500)
                }
            } else if (state.assets.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("暂无资产", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "点击 + 添加第一件资产",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.assets, key = { it.id }) { asset ->
                        AssetListItem(
                            asset, state.deadlines[asset.id],
                            isSelected = asset.id in selectedIds,
                            onLongClick = {
                                if (selectedIds.isEmpty()) selectedIds = setOf(asset.id)
                                else selectedIds = if (asset.id in selectedIds) selectedIds - asset.id else selectedIds + asset.id
                            },
                            onClick = {
                                if (selectedIds.isNotEmpty()) {
                                    selectedIds = if (asset.id in selectedIds) selectedIds - asset.id else selectedIds + asset.id
                                    if (selectedIds.isEmpty()) selectedIds = emptySet()
                                } else {
                                    onAssetClick(asset.id)
                                }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        ZichanFAB(
            onClick = onAddAsset,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Amber500,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Add, "添加资产")
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun AssetListItem(asset: AssetEntity, deadline: Long?, isSelected: Boolean, onLongClick: () -> Unit, onClick: () -> Unit) {
    val statusColor = when (asset.status) {
        "使用中" -> StatusInUse
        "闲置" -> StatusIdle
        "已借出" -> StatusLent
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    ZichanCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Amber500.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(Icons.Filled.Check, null, Modifier.size(22.dp), tint = Amber500)
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    if (asset.status == "已借出" && deadline != null && deadline > 0) {
                        val days = ((deadline - System.currentTimeMillis()) / (24 * 3600 * 1000)).toInt()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(asset.status, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (days >= 0) "${days}天" else "逾期",
                                fontSize = 10.sp,
                                color = if (days >= 0) Amber500 else StatusRed
                            )
                        }
                    } else {
                        Text(
                            asset.status,
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    if (asset.brand.isNotBlank()) {
                        Text("${asset.brand} ", style = MaterialTheme.typography.bodySmall)
                    }
                    if (asset.model.isNotBlank()) {
                        Text(asset.model, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                NumberFormat.getCurrencyInstance(Locale.CHINA).format(asset.price),
                style = MaterialTheme.typography.titleMedium,
                color = Amber500
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipRow(
    filterStatuses: Set<String>,
    onToggleStatus: (String) -> Unit,
    onClear: () -> Unit
) {
    val statuses = listOf("使用中", "闲置", "已借出", "已出售", "已丢弃")
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { status ->
            val selected = status in filterStatuses
            FilterChip(
                selected = selected,
                onClick = { onToggleStatus(status) },
                label = { Text(status, style = MaterialTheme.typography.bodySmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Amber500.copy(alpha = 0.15f),
                    selectedLabelColor = Amber500,
                )
            )
        }
        if (filterStatuses.isNotEmpty()) {
            FilterChip(
                selected = false,
                onClick = onClear,
                label = { Text("清除", style = MaterialTheme.typography.bodySmall) }
            )
        }
    }
}
