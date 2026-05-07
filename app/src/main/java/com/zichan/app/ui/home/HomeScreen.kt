package com.zichan.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.StatusGreen
import com.zichan.app.ui.theme.StatusRed
import com.zichan.app.ui.theme.StatusYellow
import com.zichan.app.ui.theme.TextSecondary
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanFAB
import com.zichan.app.ui.util.ZichanTopBar
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onAssetClick: (Long) -> Unit = {},
    onAddAsset: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ZichanTopBar(title = "资产管家")

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Amber500)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { TotalValueCard(uiState.totalValue) }
                    item { StatusRow(uiState.inUseCount, uiState.idleCount, uiState.lentCount) }

                    if (uiState.expiringAssets.isNotEmpty()) {
                        item { Spacer(Modifier.height(8.dp)) }
                        item {
                            Text(
                                "即将到期",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                            )
                        }
                        items(uiState.expiringAssets) { asset ->
                            ExpiryCard(asset, onClick = { onAssetClick(asset.id) })
                        }
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
fun TotalValueCard(value: Double) {
    ZichanCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Amber500.copy(alpha = 0.10f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "总资产",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    NumberFormat.getCurrencyInstance(Locale.CHINA).format(value),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Amber500
                )
            }
        }
    }
}

@Composable
fun StatusRow(inUse: Int, idle: Int, lent: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatusChip("使用中", inUse, StatusGreen, Modifier.weight(1f))
        StatusChip("闲置", idle, StatusYellow, Modifier.weight(1f))
        StatusChip("已借出", lent, StatusRed, Modifier.weight(1f))
    }
}

@Composable
fun StatusChip(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier
) {
    ZichanCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ExpiryCard(asset: AssetEntity, onClick: () -> Unit) {
    ZichanCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("!", color = StatusRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    asset.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "即将到期",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
