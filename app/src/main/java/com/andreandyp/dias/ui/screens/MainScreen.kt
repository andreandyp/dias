package com.andreandyp.dias.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andreandyp.dias.R
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.ui.state.AlarmUiState
import com.andreandyp.dias.ui.state.MainState
import com.andreandyp.dias.ui.theme.DiasTheme
import com.andreandyp.dias.ui.utils.ComposeUtils
import com.andreandyp.dias.viewmodels.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, onRefresh: () -> Unit, onClickSettings: () -> Unit) {
    val alarmsState = viewModel.alarms
    val state by viewModel.state.collectAsState()

    MainLayout(
        state = state,
        alarms = alarmsState,
        onRefresh = onRefresh,
        onClickMenu = viewModel::onClickMenu,
        onClickSettings = onClickSettings,
        onClickExpand = viewModel::onClickExpand,
        onChangeAlarmOnOff = viewModel::onChangeAlarmOnOff,
        onChangeVibration = viewModel::onChangeVibration,
        onChangeRingtone = {},
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MainLayout(
    state: MainState,
    alarms: List<AlarmUiState>,
    onRefresh: () -> Unit,
    onClickMenu: (Boolean) -> Unit,
    onClickSettings: () -> Unit,
    onClickExpand: (AlarmUiState) -> Unit,
    onChangeAlarmOnOff: (Boolean, AlarmUiState) -> Unit,
    onChangeVibration: (Boolean, AlarmUiState) -> Unit,
    onChangeRingtone: (AlarmUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(state.loading, onRefresh)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "")
                    }
                    IconButton(onClick = {
                        onClickMenu(true)
                    }) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
                    }
                    DropdownMenu(
                        expanded = state.isOpenActionsMenu,
                        onDismissRequest = { onClickMenu(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.settings)) },
                            onClick = {
                                onClickMenu(false)
                                onClickSettings()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Settings,
                                    contentDescription = stringResource(id = R.string.settings),
                                )
                            },
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .padding(paddingValues),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Header(state = state)
                OriginLabel(state)
                Divider(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                LazyColumn {
                    items(alarms) {
                        AlarmConfigItem(
                            uiState = it,
                            onClickExpand = onClickExpand,
                            onChangeAlarmOnOff = onChangeAlarmOnOff,
                            onChangeVibration = onChangeVibration,
                            onChangeRingtone = onChangeRingtone,
                        )
                    }

                    item {
                        Text(
                            text = stringResource(id = R.string.api_credits),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            PullRefreshIndicator(
                state.loading,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun Header(state: MainState) {
    val nextAlarm = state.nextAlarm ?: stringResource(id = R.string.no_next_alarm)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.next_alarm_headline),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = nextAlarm,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun OriginLabel(state: MainState) {
    val textId = when (state.origin) {
        Origin.INTERNET -> R.string.origin_internet
        Origin.DATABASE -> R.string.origin_bd
        null -> R.string.no_origin
        else -> R.string.origin_default
    }
    AnimatedContent(targetState = state.loading, label = "Origin") {
        if (it) {
            Text(
                text = stringResource(id = R.string.origin_loading),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = stringResource(id = textId),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AlarmConfigItem(
    uiState: AlarmUiState,
    onClickExpand: (AlarmUiState) -> Unit,
    onChangeAlarmOnOff: (Boolean, AlarmUiState) -> Unit,
    onChangeVibration: (Boolean, AlarmUiState) -> Unit,
    onChangeRingtone: (AlarmUiState) -> Unit,
) {
    val colors = if (uiState.isConfigExpanded) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .selectable(selected = uiState.isConfigExpanded, onClick = { onClickExpand(uiState) })
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = colors),
        elevation = CardDefaults.cardElevation(defaultElevation = if (uiState.isConfigExpanded) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                DayAndOffset(uiState)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ConfigurationSwitches(uiState, onChangeAlarmOnOff, onChangeVibration)
            }
        }
        ExpandedConfiguration(uiState, onChangeRingtone)
    }
}

@Composable
private fun DayAndOffset(alarm: AlarmUiState) {
    Text(
        text = alarm.formattedDay,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp),
        // si es alarma siguiente, cambiar color
    )
    TextButton(onClick = {}) {
        Text(
            text = alarm.formattedOffset,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp),
            // si es alarma siguiente, cambiar color
        )
    }
}

@Composable
private fun ConfigurationSwitches(
    alarm: AlarmUiState,
    onChangeAlarmOnOff: (Boolean, AlarmUiState) -> Unit,
    onChangeVibration: (Boolean, AlarmUiState) -> Unit,
) {
    Row(
        modifier = Modifier.padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = stringResource(id = R.string.turn_alarm_config))
        Switch(checked = alarm.isOn, onCheckedChange = { onChangeAlarmOnOff(it, alarm) })
    }
    Row(
        modifier = Modifier.padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = stringResource(id = R.string.vibration_alarm_config))
        Switch(checked = alarm.shouldVibrate, onCheckedChange = { onChangeVibration(it, alarm) })
    }
}

@Composable
private fun ExpandedConfiguration(alarm: AlarmUiState, onChangeRingtone: (AlarmUiState) -> Unit) {
    AnimatedVisibility(
        visible = alarm.isConfigExpanded,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextButton(onClick = { onChangeRingtone(alarm) }) {
            Text(text = stringResource(id = R.string.choose_song_alarm_config))
        }
    }
    Crossfade(targetState = alarm.isConfigExpanded, label = "Arrow") {
        if (it) {
            Icon(
                imageVector = Icons.Rounded.ArrowDropUp,
                contentDescription = stringResource(id = R.string.alarm_collapse_desc),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = stringResource(id = R.string.alarm_expand_desc),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainLayoutPreview() {
    val alarm = ComposeUtils.alarmUiStatePreview
    DiasTheme {
        Surface {
            MainLayout(
                MainState(origin = Origin.DATABASE),
                listOf(
                    alarm,
                    alarm.copy(isConfigExpanded = true),
                    alarm,
                    alarm,
                    alarm,
                    alarm.copy(isConfigExpanded = true),
                    alarm,
                ),
                onRefresh = {},
                onClickSettings = {},
                onClickMenu = {},
                onClickExpand = {},
                onChangeAlarmOnOff = { _, _ -> },
                onChangeVibration = { _, _ -> },
                onChangeRingtone = {},
            )
        }
    }
}
