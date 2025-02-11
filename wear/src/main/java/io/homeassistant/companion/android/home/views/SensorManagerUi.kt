package io.homeassistant.companion.android.home.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.rememberScalingLazyListState
import io.homeassistant.companion.android.common.sensors.SensorManager
import io.homeassistant.companion.android.database.sensor.Sensor
import io.homeassistant.companion.android.theme.WearAppTheme
import io.homeassistant.companion.android.util.batterySensorManager
import io.homeassistant.companion.android.views.ListHeader
import io.homeassistant.companion.android.views.ThemeLazyColumn

@Composable
fun SensorManagerUi(
    allSensors: List<Sensor>?,
    sensorManager: SensorManager,
    onSensorClicked: (String, Boolean) -> Unit,
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    val context = LocalContext.current
    val availableSensors by remember {
        mutableStateOf(
            sensorManager
                .getAvailableSensors(context)
                .sortedBy { context.getString(it.name) }
        )
    }
    WearAppTheme {
        Scaffold(
            positionIndicator = {
                if (scalingLazyListState.isScrollInProgress)
                    PositionIndicator(scalingLazyListState = scalingLazyListState)
            },
            timeText = { TimeText(!scalingLazyListState.isScrollInProgress) }
        ) {
            ThemeLazyColumn(
                state = scalingLazyListState
            ) {
                item {
                    ListHeader(id = sensorManager.name)
                }
                val currentSensors = allSensors?.filter { sensor ->
                    availableSensors.firstOrNull { availableSensor ->
                        sensor.id == availableSensor.id
                    } != null
                }

                items(availableSensors.size, { availableSensors[it].id }) { index ->
                    val basicSensor = availableSensors[index]
                    val sensor = currentSensors?.firstOrNull { sensor ->
                        sensor.id == basicSensor.id
                    }
                    SensorUi(
                        sensor = sensor,
                        manager = sensorManager,
                        basicSensor = basicSensor,
                    ) { sensorId, enabled -> onSensorClicked(sensorId, enabled) }
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun PreviewSensorManagerUI() {
    CompositionLocalProvider {
        SensorManagerUi(
            allSensors = listOf(),
            sensorManager = batterySensorManager
        ) { _, _ -> }
    }
}
