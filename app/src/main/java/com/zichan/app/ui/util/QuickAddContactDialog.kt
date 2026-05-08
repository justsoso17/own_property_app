package com.zichan.app.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zichan.app.ui.theme.Amber500

@Composable
fun QuickAddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, relationship: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(12.dp)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Amber500.copy(alpha = 0.5f),
        focusedLabelColor = Amber500,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("快速添加联系人") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = shape,
                    colors = colors,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("手机号（选填）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = shape,
                    colors = colors,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("关系（选填）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = shape,
                    colors = colors,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(name.trim(), phone.trim(), relationship.trim()) },
                enabled = name.isNotBlank()
            ) {
                Text("添加", color = Amber500)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
