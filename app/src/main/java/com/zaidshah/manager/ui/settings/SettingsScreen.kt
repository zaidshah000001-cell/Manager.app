package com.zaidshah.manager.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidshah.manager.R
import com.zaidshah.manager.ui.components.PresetAvatarChooser
import com.zaidshah.manager.ui.theme.DarkText
import androidx.compose.material3.MaterialTheme
import com.zaidshah.manager.ui.theme.MutedText
import com.zaidshah.manager.ui.theme.SageTrack
import com.zaidshah.manager.ui.theme.WarmCream

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAvatarChooser by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    if (showColorPicker) {
        AccentColorPicker(
            initialHex = uiState.customPrimary ?: "#356B54",
            onDismiss = { showColorPicker = false },
            onApply = { viewModel.applyAccent(it); showColorPicker = false }
        )
    }

    if (showAvatarChooser) {
        PresetAvatarChooser(
            selectedId = uiState.avatarId,
            onSelect = { viewModel.updateAvatarId(it) },
            onDismiss = { showAvatarChooser = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.nav_settings),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = stringResource(R.string.settings_subtitle),
                fontSize = 13.sp,
                color = MutedText
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card A: Profile
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SageTrack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = uiState.profileName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = stringResource(R.string.local_profile),
                        fontSize = 12.sp,
                        color = MutedText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.choose_preset_avatars),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showAvatarChooser = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card B: Appearance
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.appearance),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = stringResource(R.string.appearance_helper),
                    fontSize = 12.sp,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(14.dp))

                listOf(
                    Pair("FOREST", stringResource(R.string.theme_forest)),
                    Pair("NAVY_CREAM", "Navy Blue & Cream"),
                    Pair("IVORY_NAVY", "Ivory + Navy Blue"),
                    Pair("MIDNIGHT", stringResource(R.string.theme_midnight)),
                    Pair("OCEAN", stringResource(R.string.theme_ocean))
                ).forEach { (preset, label) ->
                    val isSelected = uiState.themePreset == preset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setThemePreset(preset) }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = DarkText
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom accent selection: cream stays fixed.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Custom Colours", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text("Choose an accent · Cream background stays fixed", fontSize = 12.sp, color = MutedText)
                Spacer(modifier = Modifier.height(16.dp))
                val defaults = listOf("#356B54", "#003D74", "#800020", "#673AB7", "#C05A28")
                val swatches = (uiState.recentColors + defaults).distinct().take(5)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    swatches.forEach { hex ->
                        val swatch = remember(hex) { runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray) }
                        Box(
                            modifier = Modifier.size(48.dp)
                                .clip(CircleShape)
                                .background(swatch)
                                .border(
                                    if (uiState.themePreset == "CUSTOM" && uiState.customPrimary.equals(hex, true)) 3.dp else 1.dp,
                                    if (uiState.themePreset == "CUSTOM" && uiState.customPrimary.equals(hex, true)) Color.Black else Color.LightGray,
                                    CircleShape
                                )
                                .clickable { viewModel.applyAccent(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.themePreset == "CUSTOM" && uiState.customPrimary.equals(hex, true)) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                            .clickable { showColorPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("More +", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card D: Preferences
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.preferences),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(14.dp))

                val prefsItems = listOf(
                    Pair(stringResource(R.string.language), "English / Hindi"),
                    Pair(stringResource(R.string.sms_detection), stringResource(R.string.sms_status)),
                    Pair(stringResource(R.string.backup_restore), stringResource(R.string.backup_status)),
                    Pair(stringResource(R.string.app_info), stringResource(R.string.app_version))
                )

                prefsItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = item.first, fontSize = 14.sp, color = DarkText)
                            Text(text = item.second, fontSize = 11.sp, color = MutedText)
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MutedText
                        )
                    }
                    if (index < prefsItems.lastIndex) {
                        Divider(color = SageTrack.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}

@Composable
private fun AccentColorPicker(
    initialHex: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    val initial = remember(initialHex) {
        runCatching { android.graphics.Color.parseColor(initialHex) }.getOrDefault(android.graphics.Color.rgb(53,107,84))
    }
    val hsv = remember(initial) { FloatArray(3).also { android.graphics.Color.colorToHSV(initial, it) } }
    var hue by remember(initial) { mutableStateOf(hsv[0]) }
    var saturation by remember(initial) { mutableStateOf(hsv[1]) }
    var brightness by remember(initial) { mutableStateOf(hsv[2]) }
    var manualHex by remember { mutableStateOf("") }
    val selected = remember(hue, saturation, brightness) {
        Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness)))
    }
    val hex = remember(selected) { String.format("#%06X", selected.toArgb() and 0xFFFFFF) }
    val hueColors = remember {
        (0..12).map { i -> Color(android.graphics.Color.HSVToColor(floatArrayOf(i * 30f, 1f, 1f))) }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose accent colour", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Cream background will not change", fontSize = 12.sp, color = MutedText)
                Spacer(Modifier.height(12.dp))
                Canvas(
                    modifier = Modifier.size(240.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { p ->
                                val dx = p.x - size.width / 2f
                                val dy = p.y - size.height / 2f
                                hue = ((atan2(dy, dx) * 180f / PI.toFloat() + 90f + 360f) % 360f)
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val dx = change.position.x - size.width / 2f
                                val dy = change.position.y - size.height / 2f
                                hue = ((atan2(dy, dx) * 180f / PI.toFloat() + 90f + 360f) % 360f)
                                change.consume()
                            }
                        }
                ) {
                    val radius = size.minDimension / 2f - 18.dp.toPx()
                    drawCircle(
                        brush = Brush.sweepGradient(hueColors),
                        radius = radius,
                        style = Stroke(width = 32.dp.toPx())
                    )
                    drawCircle(color = selected, radius = radius * 0.67f)
                    val angle = (hue - 90f) * PI.toFloat() / 180f
                    val point = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
                    drawCircle(Color.White, radius = 10.dp.toPx(), center = point)
                    drawCircle(Color.DarkGray, radius = 10.dp.toPx(), center = point, style = Stroke(1.5.dp.toPx()))
                }
                Spacer(Modifier.height(12.dp))
                Text("Saturation", modifier = Modifier.fillMaxWidth(), fontSize = 13.sp)
                Slider(value = saturation, onValueChange = { saturation = it })
                Text("Brightness", modifier = Modifier.fillMaxWidth(), fontSize = 13.sp)
                Slider(value = brightness, onValueChange = { brightness = it })
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(selected))
                    Text(hex, fontWeight = FontWeight.Bold)
                }
                OutlinedTextField(
                    value = manualHex,
                    onValueChange = { input ->
                        manualHex = input.take(7)
                        if (manualHex.matches(Regex("#[0-9A-Fa-f]{6}"))) {
                            val parsed = android.graphics.Color.parseColor(manualHex)
                            val values = FloatArray(3)
                            android.graphics.Color.colorToHSV(parsed, values)
                            hue = values[0]; saturation = values[1]; brightness = values[2]
                        }
                    },
                    label = { Text("HEX code (optional)") },
                    placeholder = { Text("#356B54") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(onClick = { onApply(hex) }) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
