package com.zichan.app.ui.asset

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.util.ZichanButton
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar

private val FieldShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetEditScreen(
    assetId: Long,
    onBack: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.editState.collectAsStateWithLifecycle()
    var categoryExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(assetId) { viewModel.loadAssetForEdit(assetId) }
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(
            title = if (assetId == 0L) "添加资产" else "编辑资产",
            onBack = onBack
        )

        if (state.isLoading) {
            Column(Modifier.fillMaxSize()) {
                CircularProgressIndicator(color = Amber500)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name - required
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateEditField { copy(name = it) } },
                    label = { Text("名称 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = FieldShape,
                    colors = fieldColors()
                )

                // Brand + Model side by side
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.brand,
                        onValueChange = { viewModel.updateEditField { copy(brand = it) } },
                        label = { Text("品牌") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = FieldShape,
                        colors = fieldColors()
                    )
                    OutlinedTextField(
                        value = state.model,
                        onValueChange = { viewModel.updateEditField { copy(model = it) } },
                        label = { Text("型号") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = FieldShape,
                        colors = fieldColors()
                    )
                }

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.categories.find { it.id == state.categoryId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("分类") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = FieldShape,
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        state.categories.forEach { cat ->
                            val isSelected = cat.id == state.categoryId
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = categoryIcon(cat.name),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            cat.name,
                                            color = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.updateEditField { copy(categoryId = cat.id) }
                                    categoryExpanded = false
                                },
                                trailingIcon = {
                                    if (isSelected) Icon(
                                        Icons.Filled.Check, "已选",
                                        modifier = Modifier.size(18.dp),
                                        tint = Amber500
                                    )
                                }
                            )
                        }
                    }
                }

                // Price
                OutlinedTextField(
                    value = state.price,
                    onValueChange = { viewModel.updateEditField { copy(price = it) } },
                    label = { Text("价格 (元)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = FieldShape,
                    colors = fieldColors()
                )

                // Purchase channel
                OutlinedTextField(
                    value = state.purchaseChannel,
                    onValueChange = { viewModel.updateEditField { copy(purchaseChannel = it) } },
                    label = { Text("购买渠道") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = FieldShape,
                    colors = fieldColors()
                )

                // Status dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("状态") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = FieldShape,
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        STATUS_OPTIONS.forEach { (label, icon) ->
                            val isSelected = label == state.status
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(icon, null, Modifier.size(20.dp),
                                            tint = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(12.dp))
                                        Text(label, color = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurface)
                                    }
                                },
                                onClick = {
                                    viewModel.updateEditField { copy(status = label) }
                                    statusExpanded = false
                                },
                                trailingIcon = {
                                    if (isSelected) Icon(Icons.Filled.Check, "已选", Modifier.size(18.dp), tint = Amber500)
                                }
                            )
                        }
                    }
                }

                // Location dropdown
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.locations.find { it.id == state.locationId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("位置") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = FieldShape,
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        state.locations.forEach { loc ->
                            val isSelected = loc.id == state.locationId
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = locationIcon(loc.name),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            loc.name,
                                            color = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.updateEditField { copy(locationId = loc.id) }
                                    locationExpanded = false
                                },
                                trailingIcon = {
                                    if (isSelected) Icon(Icons.Filled.Check, "已选", Modifier.size(18.dp), tint = Amber500)
                                }
                            )
                        }
                    }
                }

                // Specs
                OutlinedTextField(
                    value = state.specs,
                    onValueChange = { viewModel.updateEditField { copy(specs = it) } },
                    label = { Text("规格参数") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = FieldShape,
                    colors = fieldColors()
                )

                // Serial number
                OutlinedTextField(
                    value = state.serialNumber,
                    onValueChange = { viewModel.updateEditField { copy(serialNumber = it) } },
                    label = { Text("序列号") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = FieldShape,
                    colors = fieldColors()
                )

                // Notes
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { viewModel.updateEditField { copy(notes = it) } },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = FieldShape,
                    colors = fieldColors()
                )

                // Virtual asset toggle
                ZichanCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("虚拟资产", Modifier.weight(1f))
                        Switch(
                            checked = state.isVirtual,
                            onCheckedChange = { viewModel.updateEditField { copy(isVirtual = it) } }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Save button
                ZichanButton(
                    onClick = { viewModel.saveAsset(assetId) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = state.name.isNotBlank() && !state.isSaving,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber500,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Amber500.copy(alpha = 0.3f),
                    )
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp))
                    } else {
                        Text("保存", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Amber500.copy(alpha = 0.5f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = Amber500,
)

private val STATUS_OPTIONS = listOf(
    "使用中" to Icons.Outlined.CheckCircle,
    "闲置" to Icons.Outlined.RemoveCircle,
    "已借出" to Icons.Filled.ChevronRight,
    "已出售" to Icons.Filled.ShoppingCart,
    "已丢弃" to Icons.Filled.Delete,
)

private fun categoryIcon(name: String) = when (name) {
    "电子产品" -> Icons.Filled.PhoneAndroid
    "家具家居" -> Icons.Filled.Chair
    "收藏品" -> Icons.Filled.Diamond
    "图书" -> Icons.Outlined.Book
    "服装" -> Icons.Filled.BusinessCenter
    "软件" -> Icons.Outlined.Code
    "订阅服务" -> Icons.Outlined.Subscriptions
    "数字账号" -> Icons.Outlined.AccountCircle
    "域名" -> Icons.Filled.Language
    else -> Icons.Outlined.MoreHoriz
}

private fun locationIcon(name: String) = when (name) {
    "卧室" -> Icons.Filled.Weekend
    "客厅" -> Icons.Filled.Weekend
    "书房" -> Icons.Filled.MenuBook
    "办公室" -> Icons.Filled.Business
    "父母家" -> Icons.Filled.Home
    else -> Icons.Filled.MoreHoriz
}
