package com.zichan.app.ui.stats

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.StatusGreen
import com.zichan.app.ui.theme.StatusRed
import com.zichan.app.ui.theme.TextSecondary
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(title = "统计")
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Amber500)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Value summary
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ValueCard("购置总价", state.totalValue, Amber500, Modifier.weight(1f))
                        ValueCard("折旧现值", state.depreciatedValue, StatusGreen, Modifier.weight(1f))
                    }
                }

                // Depreciation loss if any
                if (state.depreciationLoss > 0) {
                    item {
                        ZichanCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = StatusRed.copy(alpha = 0.08f)
                            )
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("累计折旧损失", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text(
                                        NumberFormat.getCurrencyInstance(Locale.CHINA).format(state.depreciationLoss),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusRed
                                    )
                                }
                            }
                        }
                    }
                }

                // Category breakdown
                item {
                    Text("分类价值", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                items(state.categoryStats) { stat ->
                    CategoryBar(
                        name = stat.name,
                        value = stat.total,
                        percentage = if (state.totalValue > 0) (stat.total / state.totalValue).toFloat() else 0f
                    )
                }

                // Top depreciated items
                if (state.topDepreciated.isNotEmpty()) {
                    item { Spacer(Modifier.height(4.dp)) }
                    item {
                        Text("折旧明细", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    items(state.topDepreciated) { info ->
                        DepreciationRow(info)
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun ValueCard(label: String, value: Double, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    ZichanCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(
                NumberFormat.getCurrencyInstance(Locale.CHINA).format(value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun CategoryBar(name: String, value: Double, percentage: Float) {
    ZichanCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    NumberFormat.getCurrencyInstance(Locale.CHINA).format(value),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { percentage.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Amber500,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun DepreciationRow(info: DepreciationInfo) {
    ZichanCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(info.assetName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text("${info.yearsOwned}年前购入 · ${info.categoryName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    NumberFormat.getCurrencyInstance(Locale.CHINA).format(info.currentValue),
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusGreen
                )
                Text(
                    "-￥${NumberFormat.getInstance().format((info.originalPrice - info.currentValue).toLong())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusRed
                )
            }
        }
    }
}
