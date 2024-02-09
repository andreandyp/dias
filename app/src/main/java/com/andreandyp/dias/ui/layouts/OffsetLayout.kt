package com.andreandyp.dias.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Curtains
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.andreandyp.dias.R
import com.andreandyp.dias.domain.OffsetType
import com.andreandyp.dias.domain.asString
import com.andreandyp.dias.ui.state.AlarmUiState
import com.andreandyp.dias.ui.theme.DiasTheme
import com.andreandyp.dias.ui.utils.ComposeUtils

@Composable
fun OffsetLayout(
    alarm: AlarmUiState,
    onCancel: () -> Unit,
    onAccept: (Int, Int, OffsetType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var offsetHours by remember {
        mutableIntStateOf(alarm.offsetHours)
    }
    var offsetMinutes by remember {
        mutableIntStateOf(alarm.offsetMinutes)
    }
    var offsetType by remember {
        mutableStateOf(alarm.offsetType)
    }

    AlertDialog(
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAccept(offsetHours, offsetMinutes, offsetType!!) },
                enabled = offsetType != null
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Curtains,
                contentDescription = stringResource(id = R.string.offset_dialog_title),
            )
        },
        title = {
            Text(text = stringResource(id = R.string.offset_dialog_title))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TimeCounter(
                    text = stringResource(id = R.string.offset_dialog_hours_label),
                    value = offsetHours,
                    minValue = 0,
                    maxValue = 3,
                    steps = 1,
                    onClickDecrease = { offsetHours = it },
                    onClickIncrease = { offsetHours = it },
                )
                TimeCounter(
                    text = stringResource(id = R.string.offset_dialog_minutes_label),
                    value = offsetMinutes,
                    minValue = 0,
                    maxValue = 45,
                    steps = 15,
                    onClickDecrease = { offsetMinutes = it },
                    onClickIncrease = { offsetMinutes = it },
                )
                OffsetTypeRow(offsetType = offsetType, onSelect = { offsetType = it })
                OffsetResultLabel(offsetType, offsetHours, offsetMinutes)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun TimeCounter(
    text: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    steps: Int,
    onClickDecrease: (Int) -> Unit,
    onClickIncrease: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = text)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                if (value - steps < minValue) {
                    onClickDecrease(minValue)
                } else {
                    onClickDecrease(value - steps)
                }
            },
            enabled = value > minValue
        ) {
            Icon(imageVector = Icons.Rounded.RemoveCircle, contentDescription = "")
        }
        Text(text = value.toString())
        IconButton(
            onClick = {
                if (value + steps > maxValue) {
                    onClickIncrease(maxValue)
                } else {
                    onClickIncrease(value + steps)
                }
            },
            enabled = value < maxValue
        ) {
            Icon(imageVector = Icons.Rounded.AddCircle, contentDescription = "")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OffsetTypeRow(offsetType: OffsetType?, onSelect: (OffsetType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        FilterChip(
            selected = offsetType == OffsetType.BEFORE,
            onClick = { onSelect(OffsetType.BEFORE) },
            label = { Text(stringResource(id = R.string.offset_dialog_before_label)) },
            leadingIcon = if (offsetType == OffsetType.BEFORE) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.offset_dialog_before_label),
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
        FilterChip(
            selected = offsetType == OffsetType.AFTER,
            onClick = { onSelect(OffsetType.AFTER) },
            label = { Text(stringResource(id = R.string.offset_dialog_after_label)) },
            leadingIcon = if (offsetType == OffsetType.AFTER) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.offset_dialog_after_label),
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun OffsetResultLabel(offsetType: OffsetType?, offsetHours: Int, offsetMinutes: Int) {
    Text(
        text = stringResource(
            id = R.string.offset_dialog_result_label,
            offsetType.asString,
            offsetHours,
            if (offsetMinutes == 0) {
                stringResource(id = R.string.offset_minutes_0)
            } else {
                offsetMinutes.toString()
            },
        ),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelSmall
    )
}

@PreviewLightDark
@Composable
private fun OffsetLayoutPreview() {
    DiasTheme {
        Surface {
            OffsetLayout(
                ComposeUtils.alarmUiStatePreview.copy(
                    offsetHours = 3,
                    offsetMinutes = 0,
                    offsetType = OffsetType.BEFORE,
                ),
                {},
                { _, _, _ -> },
            )
        }
    }
}
