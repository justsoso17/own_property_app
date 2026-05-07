package com.zichan.app.ui.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
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

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ZichanTopBar(
                title = "资产",
                actions = {
                    Icon(
                        Icons.Filled.Search, "搜索",
                        modifier = Modifier.clickable { searchActive = true },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            if (searchActive) {
                SearchBar(
                    query = state.searchKeyword,
                    onQueryChange = { viewModel.search(it) },
                    onSearch = { searchActive = false },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("搜索资产...") },
                    shape = RoundedCornerShape(14.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        inputFieldColors = SearchBarDefaults.inputFieldColors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = Amber500,
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {}
            }

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
                        AssetListItem(asset, onClick = { onAssetClick(asset.id) })
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
fun AssetListItem(asset: AssetEntity, onClick: () -> Unit) {
    val statusColor = when (asset.status) {
        "使用中" -> StatusInUse
        "闲置" -> StatusIdle
        "已借出" -> StatusLent
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    ZichanCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    Text(
                        asset.status,
                        fontSize = 11.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
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
