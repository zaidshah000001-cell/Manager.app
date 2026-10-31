package com.zaidshah.manager.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidshah.manager.ui.theme.DarkText
import com.zaidshah.manager.ui.theme.ForestGreen
import com.zaidshah.manager.ui.theme.MutedText
import com.zaidshah.manager.ui.theme.SageTrack

@Composable
fun CircularProgressRing(
    percentage: Int,
    caption: String = "",
    size: Dp = 100.dp,
    strokeWidth: Dp = 7.dp,
    primaryColor: Color = ForestGreen,
    trackColor: Color = SageTrack,
    modifier: Modifier = Modifier
) {
    val progressSweep = ((percentage.coerceIn(0, 100)) / 100f) * 360f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                fontSize = if (size < 90.dp) 15.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            if (caption.isNotEmpty()) {
                Text(
                    text = caption,
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }
    }
}
