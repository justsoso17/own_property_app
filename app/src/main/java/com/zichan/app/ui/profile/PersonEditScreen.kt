package com.zichan.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.util.ZichanButton
import com.zichan.app.ui.util.ZichanTopBar

private val PersonFieldShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonEditScreen(
    personId: Long,
    onBack: () -> Unit = {},
    viewModel: PersonViewModel = hiltViewModel()
) {
    val state by viewModel.editState.collectAsStateWithLifecycle()

    LaunchedEffect(personId) { viewModel.loadPersonForEdit(personId) }
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(
            title = if (personId == 0L) "添加联系人" else "编辑联系人",
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
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateEditField { copy(name = it) } },
                    label = { Text("姓名 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = PersonFieldShape,
                    colors = personFieldColors()
                )
                OutlinedTextField(
                    value = state.relationship,
                    onValueChange = { viewModel.updateEditField { copy(relationship = it) } },
                    label = { Text("关系") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = PersonFieldShape,
                    colors = personFieldColors()
                )
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { viewModel.updateEditField { copy(phone = it) } },
                    label = { Text("手机号") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = PersonFieldShape,
                    colors = personFieldColors()
                )
                OutlinedTextField(
                    value = state.wechat,
                    onValueChange = { viewModel.updateEditField { copy(wechat = it) } },
                    label = { Text("微信") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = PersonFieldShape,
                    colors = personFieldColors()
                )
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { viewModel.updateEditField { copy(notes = it) } },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = PersonFieldShape,
                    colors = personFieldColors()
                )
                Spacer(Modifier.height(4.dp))
                ZichanButton(
                    onClick = { viewModel.savePerson(personId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = state.name.isNotBlank() && !state.isSaving,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
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
private fun personFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Amber500.copy(alpha = 0.5f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = Amber500,
)
