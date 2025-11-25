package com.bbqreset.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbqreset.ui.design.system.LocalDSColors
import com.bbqreset.ui.design.system.LocalDSShapes

enum class BBQButtonVariant { PRIMARY, SECONDARY, OUTLINE, GHOST, DESTRUCTIVE }

@Composable
fun BBQButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: BBQButtonVariant = BBQButtonVariant.PRIMARY,
    enabled: Boolean = true,
    shape: Shape = LocalDSShapes.current.chip,
    leadingContent: (@Composable (() -> Unit))? = null,
    trailingContent: (@Composable (() -> Unit))? = null,
    onClick: () -> Unit
) {
    val ds = LocalDSColors.current
    when (variant) {
        BBQButtonVariant.PRIMARY -> FilledButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            background = ds.primary,
            contentColor = ds.onPrimary,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            onClick = onClick
        )
        BBQButtonVariant.SECONDARY -> FilledButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            background = ds.muted,
            contentColor = ds.mutedForeground,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            onClick = onClick
        )
        BBQButtonVariant.DESTRUCTIVE -> FilledButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            background = ds.destructive,
            contentColor = ds.onDestructive,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            onClick = onClick
        )
        BBQButtonVariant.OUTLINE -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            border = BorderStroke(1.dp, ds.border),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ds.onSurface
            )
        ) {
            ButtonContent(text, leadingContent, trailingContent)
        }
        BBQButtonVariant.GHOST -> TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = ds.onSurface
            )
        ) { ButtonContent(text, leadingContent, trailingContent) }
    }
}

@Composable
private fun FilledButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    background: Color,
    contentColor: Color,
    leadingContent: (@Composable (() -> Unit))?,
    trailingContent: (@Composable (() -> Unit))?,
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = contentColor,
            disabledContainerColor = background.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        ButtonContent(text, leadingContent, trailingContent)
    }
}

@Composable
private fun ButtonContent(
    text: String,
    leadingContent: (@Composable (() -> Unit))?,
    trailingContent: (@Composable (() -> Unit))?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        leadingContent?.invoke()
        Text(
            text = text,
            style = MaterialTheme.extendedTypography.bodyLarge.copy(
                color = LocalContentColor.current,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        )
        trailingContent?.invoke()
    }
}
