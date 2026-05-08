package com.zichan.app.ui.asset

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import com.zichan.app.ui.util.ZichanButton
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.StatusRed
import com.zichan.app.util.PhotoManager
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
    var detailStatusExpanded by remember { mutableStateOf(false) }

    // Camera for re-taking photo
    val context = LocalContext.current
    var detailPhotoFile by remember { mutableStateOf<java.io.File?>(null) }
    var detailPhotoUri by remember { mutableStateOf<Uri?>(null) }

    var borrowerName by remember { mutableStateOf<String?>(null) }
    var returnDeadline by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.reloadList()
    }

    val asset = state.assets.find { it.id == assetId }

    LaunchedEffect(asset) {
        if (asset?.status == "已借出") {
            borrowerName = viewModel.getBorrowerName(assetId)
            returnDeadline = viewModel.getReturnDeadline(assetId)
        } else {
            borrowerName = null
            returnDeadline = null
        }
    }

    val detailCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && detailPhotoFile != null) {
            val found = state.assets.find { it.id == assetId }
            if (found != null) viewModel.updatePhoto(found, detailPhotoFile!!.absolutePath)
        }
    }

    val detailPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && detailPhotoUri != null) detailCameraLauncher.launch(detailPhotoUri!!)
    }

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

                // Photo
                if (asset.photoPath != null) {
                    val bmp = remember(asset.photoPath) {
                        runCatching { BitmapFactory.decodeFile(asset.photoPath) }.getOrNull()
                    }
                    if (bmp != null) {
                        ZichanCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "资产照片",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop,
                                )
                                Row(
                                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = { viewModel.deletePhoto(asset) }) {
                                        Icon(Icons.Filled.Delete, "删除照片", tint = StatusRed, modifier = Modifier.size(22.dp))
                                    }
                                    IconButton(onClick = {
                                        val file = PhotoManager.createTempFile(context)
                                        detailPhotoFile = file
                                        detailPhotoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                        detailPermLauncher.launch(Manifest.permission.CAMERA)
                                    }) {
                                        Icon(Icons.Filled.CameraAlt, "重拍", tint = Amber500, modifier = Modifier.size(22.dp))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    ZichanCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            val file = PhotoManager.createTempFile(context)
                            detailPhotoFile = file
                            detailPhotoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            detailPermLauncher.launch(Manifest.permission.CAMERA)
                        }
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CameraAlt, null, Modifier.size(22.dp), tint = Amber500)
                            Spacer(Modifier.width(10.dp))
                            Text("添加照片", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                ZichanCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        // Editable status dropdown
                        ExposedDropdownMenuBox(
                            expanded = detailStatusExpanded,
                            onExpandedChange = { detailStatusExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = asset.status,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("状态") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = detailStatusExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Amber500.copy(alpha = 0.5f),
                                    focusedLabelColor = Amber500,
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium
                            )
                            ExposedDropdownMenu(
                                expanded = detailStatusExpanded,
                                onDismissRequest = { detailStatusExpanded = false }
                            ) {
                                listOf("使用中", "闲置", "已出售", "已丢弃").forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s, color = if (s == asset.status) Amber500 else MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            viewModel.updateStatus(asset, s)
                                            detailStatusExpanded = false
                                        },
                                        trailingIcon = {
                                            if (s == asset.status) Icon(Icons.Filled.Check, "当前", Modifier.size(18.dp), tint = Amber500)
                                        }
                                    )
                                }
                            }
                        }
                        if (asset.status == "已借出" && borrowerName != null) {
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("借出给: $borrowerName", style = MaterialTheme.typography.bodySmall, color = Amber500)
                                if (returnDeadline != null) {
                                    val daysLeft = ((returnDeadline!! - System.currentTimeMillis()) / (24 * 3600 * 1000)).toInt()
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (daysLeft >= 0) "剩余${daysLeft}天" else "已逾期${-daysLeft}天",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (daysLeft >= 0) Amber500 else StatusRed
                                    )
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                        }
                        Spacer(Modifier.height(4.dp))
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
