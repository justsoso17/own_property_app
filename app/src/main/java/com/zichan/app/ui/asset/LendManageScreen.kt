package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.zichan.app.ui.util.ZichanButton
import com.zichan.app.ui.util.ZichanTopBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Orange500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendManageScreen(
    assetId: Long,
    onBack: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    val asset = state.assets.find { it.id == assetId }
    var selectedPersonId by remember { mutableStateOf<Long?>(null) }
    var personExpanded by remember { mutableStateOf(false) }
    var expectedReturnDays by remember { mutableStateOf("7") }

    // We need persons - using simple approach for now
    Column(Modifier.fillMaxSize()) {
        ZichanTopBar(title = "借出资产", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (asset != null) {
                Text("借出: ${asset.name}", style = MaterialTheme.typography.headlineSmall)
            }

            OutlinedTextField(
                value = expectedReturnDays,
                onValueChange = { expectedReturnDays = it },
                label = { Text("预计归还天数") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            ZichanButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange500)
            ) {
                Text("确认借出", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}