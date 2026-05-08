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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.zichan.app.data.entity.PersonEntity
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.util.QuickAddContactDialog
import com.zichan.app.ui.util.ZichanButton
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanTopBar

private val LendFieldShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendManageScreen(
    assetId: Long,
    onBack: () -> Unit = {},
    viewModel: LendViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var personExpanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        QuickAddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, rel ->
                viewModel.addPerson(PersonEntity(name = name, phone = phone, relationship = rel))
                showAddDialog = false
            }
        )
    }

    LaunchedEffect(assetId) { viewModel.load(assetId) }
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(title = "借出资产", onBack = onBack)

        if (state.isLoading) {
            Column(Modifier.fillMaxSize()) { /* loading handled by LaunchedEffect */ }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // Asset info card
                state.asset?.let { asset ->
                    ZichanCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "借出物品",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                asset.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (asset.brand.isNotBlank() || asset.model.isNotBlank()) {
                                Text(
                                    "${asset.brand} ${asset.model}".trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Person dropdown
                ExposedDropdownMenuBox(
                    expanded = personExpanded,
                    onExpandedChange = { personExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.persons.find { it.id == state.selectedPersonId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("借给谁 *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = personExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = LendFieldShape,
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = personExpanded,
                        onDismissRequest = { personExpanded = false }
                    ) {
                        if (state.persons.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text("暂无联系人", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                },
                                onClick = { personExpanded = false },
                                enabled = false
                            )
                        }
                        state.persons.forEach { person ->
                            val isSelected = person.id == state.selectedPersonId
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Person, null, Modifier.size(20.dp),
                                            tint = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(person.name, color = if (isSelected) Amber500 else MaterialTheme.colorScheme.onSurface)
                                            if (person.phone.isNotBlank()) Text(person.phone,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                },
                                onClick = { viewModel.selectPerson(person.id); personExpanded = false },
                                trailingIcon = {
                                    if (isSelected) Icon(Icons.Filled.Check, "已选", Modifier.size(18.dp), tint = Amber500)
                                }
                            )
                        }
                        // Add contact entry
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Add, null, Modifier.size(20.dp), tint = Amber500)
                                    Spacer(Modifier.width(12.dp))
                                    Text("添加联系人", color = Amber500)
                                }
                            },
                            onClick = {
                                personExpanded = false
                                showAddDialog = true
                            }
                        )
                    }
                }

                // Return days
                OutlinedTextField(
                    value = state.returnDays,
                    onValueChange = { viewModel.setReturnDays(it) },
                    label = { Text("几天后归还") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = LendFieldShape,
                    colors = fieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(8.dp))

                // Confirm button
                ZichanButton(
                    onClick = { viewModel.lend() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = state.selectedPersonId != null && !state.isSaving,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber500,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Amber500.copy(alpha = 0.3f),
                    )
                ) {
                    Text(if (state.isSaving) "借出中..." else "确认借出", style = MaterialTheme.typography.titleMedium)
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
