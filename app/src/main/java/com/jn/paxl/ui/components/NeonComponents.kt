package com.jn.paxl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.paxl.ui.theme.BackgroundDark
import com.jn.paxl.ui.theme.PressStart2PFamily

val RetroFont = PressStart2PFamily

@Composable
fun NeonButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(vertical = 4.dp) // Spacing between buttons
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
                .border(2.dp, color, shape)
                .clickable { onClick() }
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
            Text(
                text = text,
                color = color,
                fontSize = 16.sp,
                fontFamily = RetroFont,
                style = TextStyle(
                    shadow = Shadow(
                        color = color.copy(alpha = 0.7f),
                        blurRadius = 12f
                    )
                )
            )
        }
    }
}

@Composable
fun NeonTitle(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: Int = 48
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Black,
        fontFamily = RetroFont,
        modifier = modifier,
        textAlign = TextAlign.Center,
        style = TextStyle(
            shadow = Shadow(
                color = color,
                blurRadius = 20f
            ),
            letterSpacing = 2.sp
        )
    )
}

@Composable
fun NeonText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: Int = 20,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = RetroFont,
        modifier = modifier,
        textAlign = textAlign,
        lineHeight = (fontSize * 1.5).sp,
        style = TextStyle(
            shadow = Shadow(
                color = color.copy(alpha = 0.5f),
                blurRadius = 8f
            )
        )
    )
}

@Composable
fun PaxlScreenScaffold(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 24.dp,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        val contentModifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(contentPadding)

        if (scrollable) {
            Column(
                modifier = contentModifier.verticalScroll(rememberScrollState()),
                content = content
            )
        } else {
            Column(modifier = contentModifier, content = content)
        }
    }
}

@Composable
fun PaxlBackHeader(
    title: String,
    titleColor: Color,
    titleFontSize: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 16.dp,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Spacer(Modifier.width(horizontalSpacing))
        NeonTitle(title, color = titleColor, fontSize = titleFontSize)
        Spacer(Modifier.weight(1f))
        trailing()
    }
}

