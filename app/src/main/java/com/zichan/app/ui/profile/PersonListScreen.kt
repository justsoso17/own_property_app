package com.zichan.app.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Amber500
import com.zichan.app.ui.theme.TextSecondary
import com.zichan.app.ui.util.ZichanCard
import com.zichan.app.ui.util.ZichanFAB
import com.zichan.app.ui.util.ZichanTopBar

@Composable
fun PersonListScreen(
    onPersonClick: (Long) -> Unit = {},
    onAddPerson: () -> Unit = {},
    onBack: () -> Unit = {},
    onAssetClick: (Long) -> Unit = {},
    viewModel: PersonViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ZichanTopBar(title = "联系人", onBack = onBack)
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Amber500)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.persons, key = { it.id }) { person ->
                        PersonItem(
                            person = person,
                            borrowedAssets = state.borrowedMap[person.id] ?: emptyList(),
                            onClick = { onPersonClick(person.id) },
                            onAssetClick = onAssetClick
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        ZichanFAB(
            onClick = onAddPerson,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = Amber500,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) { Icon(Icons.Filled.Add, "添加联系人") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonItem(
    person: com.zichan.app.data.entity.PersonEntity,
    borrowedAssets: List<Pair<Long, String>>,
    onClick: () -> Unit,
    onAssetClick: (Long) -> Unit
) {
    ZichanCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, null, tint = Amber500, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(person.name, style = MaterialTheme.typography.titleMedium)
                    if (person.relationship.isNotBlank()) {
                        Text(person.relationship, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                if (person.phone.isNotBlank()) {
                    Text(person.phone, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            if (borrowedAssets.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("借走:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    borrowedAssets.forEach { (assetId, name) ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Amber500.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onAssetClick(assetId) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                name,
                                style = MaterialTheme.typography.bodySmall,
                                color = Amber500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
