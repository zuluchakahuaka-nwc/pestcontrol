package com.firebug.pest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.firebug.common.plan.FloorPlanEditor
import com.firebug.common.plan.FacilityObject
import com.firebug.common.plan.PlanRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = PlanRepository(applicationContext)
        setContent { PestApp(repo) }
    }
}

@Composable
fun PestApp(repo: PlanRepository) {
    val objects by repo.objects.collectAsState()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var openId by rememberSaveable { mutableStateOf<String?>(null) }
    val openObj = openId?.let { id -> objects.firstOrNull { it.id == id } }

    BackHandler(enabled = openObj != null) { openId = null }

    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF2E7D32))) {
        Scaffold(
            bottomBar = {
                if (openObj == null) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = tab == 0,
                            onClick = { tab = 0 },
                            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                            label = { Text(stringResource(R.string.tab_objects)) }
                        )
                        NavigationBarItem(
                            selected = tab == 1,
                            onClick = { tab = 1 },
                            icon = { Icon(Icons.Filled.Calculate, contentDescription = null) },
                            label = { Text(stringResource(R.string.tab_dosage)) }
                        )
                        NavigationBarItem(
                            selected = tab == 2,
                            onClick = { tab = 2 },
                            icon = { Icon(Icons.Filled.Place, contentDescription = null) },
                            label = { Text(stringResource(R.string.tab_traps)) }
                        )
                    }
                }
            }
        ) { pad ->
            Box(Modifier.fillMaxSize().padding(pad)) {
                if (openObj != null) {
                    PlanScreenPest(
                        obj = openObj,
                        onBack = { openId = null },
                        onChange = { repo.updateObject(it) }
                    )
                } else {
                    when (tab) {
                        0 -> ObjectsTab(objects, repo, onOpen = { openId = it })
                        1 -> DosageTab()
                        2 -> TrapsTab(objects, repo)
                    }
                }
            }
        }
    }
}

@Composable
fun PlanScreenPest(
    obj: FacilityObject,
    onBack: () -> Unit,
    onChange: (FacilityObject) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Column {
                Text(obj.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(R.string.plan_header_hint),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        FloorPlanEditor(obj = obj, markerTypes = PEST_MARKER_TYPES, onObjectChange = onChange)
    }
}

@Composable
fun ObjectsTab(
    objects: List<FacilityObject>,
    repo: PlanRepository,
    onOpen: (String) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<FacilityObject?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (objects.isEmpty()) {
            Text(
                text = stringResource(R.string.list_empty),
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(objects, key = { it.id }) { o ->
                val traps = o.floors.sumOf { f -> f.markers.count { it.type in TRAP_TYPE_SET } }
                Card(modifier = Modifier.fillMaxWidth(), onClick = { onOpen(o.id) }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(o.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (o.address.isNotBlank()) {
                                Text(o.address, style = MaterialTheme.typography.bodySmall)
                            } else {
                                Text(stringResource(R.string.no_address), style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                stringResource(R.string.card_summary, o.floors.size, traps),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        IconButton(onClick = { deleteTarget = o }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(com.firebug.common.R.string.btn_delete))
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                name = ""
                address = ""
                showAdd = true
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).testTag("addObjectFab"),
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.fab_object)) }
        )
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text(stringResource(R.string.dlg_new_object)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.field_name)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(stringResource(R.string.field_address)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) repo.addObject(name.trim(), address.trim())
                    showAdd = false
                }) { Text(stringResource(R.string.btn_create)) }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false }) { Text(stringResource(com.firebug.common.R.string.btn_cancel)) }
            }
        )
    }

    deleteTarget?.let { o ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.dlg_delete_object_title)) },
            text = { Text(stringResource(R.string.dlg_delete_object_text, o.name)) },
            confirmButton = {
                TextButton(onClick = {
                    repo.deleteObject(o.id)
                    deleteTarget = null
                }) { Text(stringResource(com.firebug.common.R.string.btn_delete), color = Color(0xFFC62828)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(stringResource(com.firebug.common.R.string.btn_cancel)) }
            }
        )
    }
}
