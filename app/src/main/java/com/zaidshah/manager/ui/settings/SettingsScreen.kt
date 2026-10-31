package com.zaidshah.manager.ui.settings

import androidx.compose.foundation.background
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
import com.zaidshah.manager.ui.theme.ForestGreen
import com.zaidshah.manager.ui.theme.MutedText
import com.zaidshah.manager.ui.theme.SageTrack
import com.zaidshah.manager.ui.theme.WarmCream

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAvatarChooser by remember { mutableStateOf(false) }

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
                        tint = ForestGreen,
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
                        color = ForestGreen,
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
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card C: Custom Colours
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.custom_colours),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = stringResource(R.string.custom_colours_note),
                    fontSize = 12.sp,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.setThemePreset("FOREST") },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Primary", color = Color.White)
                    }
                    Button(
                        onClick = { viewModel.setThemePreset("FOREST") },
                        colors = ButtonDefaults.buttonColors(containerColor = SageTrack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Background", color = DarkText)
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
