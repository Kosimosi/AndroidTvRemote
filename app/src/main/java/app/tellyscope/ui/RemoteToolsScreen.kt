// SPDX-License-Identifier: GPL-3.0-only

package app.tellyscope.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.tellyscope.adb.StatsProfile
import app.tellyscope.adb.TvFolder
import app.tellyscope.data.DeviceState
import app.tellyscope.data.resolve

private enum class RemoteToolTab(val title: String) {
    Remote("Remote"),
    Monitor("Advanced Monitor"),
    Install("Install App"),
    Files("Files"),
}

/** The deliberately small connected-TV surface: Remote first, with only the three requested tools beside it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteToolsScreen(
    state: UiState,
    snackbar: SnackbarHostState,
    remoteActions: RemoteActions,
    onShowStats: (StatsProfile) -> Unit,
    onSwitchTv: () -> Unit,
    onReconnect: () -> Unit,
    onPickApp: () -> Unit,
    installActions: DeviceActions,
    onFolder: (TvFolder) -> Unit,
    onPickFile: (TvFolder) -> Unit,
    onSaveFile: (app.tellyscope.adb.TvFile) -> Unit,
    onCancelTransfer: () -> Unit,
    terminal: ShellTerminal? = null,
) {
    val device = state.selected ?: return
    var tab by rememberSaveable { mutableStateOf(RemoteToolTab.Remote) }
    LaunchedEffect(state.selectedId) { tab = RemoteToolTab.Remote }
    DisposableEffect(tab, device.id) {
        onShowStats(if (tab == RemoteToolTab.Monitor) StatsProfile.ADVANCED else StatsProfile.OTHER)
        if (tab == RemoteToolTab.Files) onFolder(state.device.files.folder)
        onDispose { onShowStats(StatsProfile.OTHER) }
    }

    Scaffold(
        containerColor = Color(0xFF0B0609),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            if (tab != RemoteToolTab.Monitor) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xCC180A0F), titleContentColor = Color(0xFFFFE9ED)),
                    title = {
                        Text(
                            when (tab) {
                                RemoteToolTab.Remote -> "TV Remote & Tools"
                                RemoteToolTab.Install -> "Install App"
                                RemoteToolTab.Files -> "Files"
                                RemoteToolTab.Monitor -> "Advanced Monitor"
                            },
                        )
                    },
                    actions = {
                        if (device.state == DeviceState.Offline) Button(onClick = onReconnect) { Text("Reconnect") }
                        TextButton(onClick = onSwitchTv) { Text("Change TV") }
                    },
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xDD180A0F), contentColor = Color(0xFFFFE9ED)) {
                RemoteToolTab.entries.forEach { destination ->
                    val icon = when (destination) {
                        RemoteToolTab.Remote -> Icons.Default.Home
                        RemoteToolTab.Monitor -> Icons.Default.Info
                        RemoteToolTab.Install -> Icons.Default.Add
                        RemoteToolTab.Files -> Icons.Default.Info
                    }
                    NavigationBarItem(
                        selected = tab == destination,
                        onClick = { tab = destination },
                        icon = { Icon(icon, contentDescription = destination.title) },
                        label = { Text(destination.title) },
                    )
                }
            }
        },
    ) { padding ->
        when (tab) {
            RemoteToolTab.Remote -> Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF35101B), Color(0xFF0B0609)))).padding(padding)) {
                RemoteScreen(
                    state = state,
                    actions = remoteActions,
                    snackbar = snackbar,
                    fullScreen = true,
                )
            }
            RemoteToolTab.Monitor -> Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF35101B), Color(0xFF0B0609)))).padding(padding)) {
                AdvancedScreen(
                    state = state,
                    back = { tab = RemoteToolTab.Remote },
                    terminal = terminal,
                    snackbar = snackbar,
                    showShell = false,
                )
            }
            RemoteToolTab.Install -> InstallAppPage(
                state = state,
                padding = padding,
                onPickApp = onPickApp,
                actions = installActions,
            )
            RemoteToolTab.Files -> FilesPage(
                state = state,
                padding = padding,
                onFolder = onFolder,
                onPickFile = onPickFile,
                onSaveFile = onSaveFile,
                onCancelTransfer = onCancelTransfer,
            )
        }
    }
}

@Composable
private fun InstallAppPage(
    state: UiState,
    padding: PaddingValues,
    onPickApp: () -> Unit,
    actions: DeviceActions,
) {
    val online = state.selected?.state == DeviceState.Online
    Column(
        Modifier.fillMaxSize().padding(padding).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Install apps on your TV")
        Text("Choose an APK, split APK set, .apks, .xapk or .apkm bundle.")
        Button(onClick = onPickApp, enabled = online) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Choose app package")
        }
        if (!online) Text("Connect to your TV to install apps.")
        if (state.device.sideload == null) {
            Text("The installer checks the package and TV version before sending it.")
        }
    }
    state.device.sideload?.let { SideloadDialog(it, actions) }
}

@Composable
private fun FilesPage(
    state: UiState,
    padding: PaddingValues,
    onFolder: (TvFolder) -> Unit,
    onPickFile: (TvFolder) -> Unit,
    onSaveFile: (app.tellyscope.adb.TvFile) -> Unit,
    onCancelTransfer: () -> Unit,
) {
    val online = state.selected?.state == DeviceState.Online
    val actions = DeviceActions(saveFile = onSaveFile)
    Column(
        Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TvFolder.entries.forEach { folder ->
                FilterChip(
                    selected = state.device.files.folder == folder,
                    onClick = { onFolder(folder) },
                    label = { Text(Copy.folder(folder).resolve()) },
                )
            }
        }
        Button(onClick = { onPickFile(state.device.files.folder) }, enabled = online) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Send file to TV")
        }
        if (!online) Text("Connect to your TV to browse or send files.")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilesBlock(state.device.files, state.clock(), actions) }
        }
    }
    state.device.transfer?.let { TransferDialog(it, onCancelTransfer) }
}
