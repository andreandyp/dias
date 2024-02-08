package com.andreandyp.dias.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.andreandyp.dias.R
import com.andreandyp.dias.ui.layouts.MainLayout
import com.andreandyp.dias.viewmodels.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, onRefresh: () -> Unit, onClickSettings: () -> Unit) {
    val alarmsState = viewModel.alarms
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val requestRingtone = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                val ringtoneUri = getRingtoneUri(it.data)
                val ringtoneTitle = getRingtoneInfo(context, ringtoneUri)

                viewModel.onRingtoneSelected(ringtoneUri, ringtoneTitle)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_ringtone_chosen),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })

    MainLayout(
        state = state,
        alarms = alarmsState,
        onRefresh = onRefresh,
        onClickMenu = viewModel::onClickMenu,
        onClickSettings = onClickSettings,
        onClickExpand = viewModel::onClickExpand,
        onChangeAlarmOnOff = viewModel::onChangeAlarmOnOff,
        onChangeVibration = viewModel::onChangeVibration,
        onChangeRingtone = { alarm ->
            viewModel.onChoosingRingtone(alarm.id)
            requestRingtone.launch(
                Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                    alarm.ringtoneName?.let {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(it))
                    }
                },
            )
        },
    )
}

@Suppress("DEPRECATION")
private fun getRingtoneUri(intent: Intent?): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)!!
    } else {
        intent!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)!!
    }
}

private fun getRingtoneInfo(context: Context, uri: Uri): String {
    return RingtoneManager.getRingtone(context, uri).getTitle(context)
}
