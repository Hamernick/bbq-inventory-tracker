package com.bbqreset.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BBQButtonVariant { PRIMARY, SECONDARY, OUTLINE, GHOST, DESTRUCTIVE }

@Composable
fun BBQButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: BBQButtonVariant = BBQButtonVariant.PRIMARY,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    leadingContent: (@Composable (() -> Unit))? = null,
    trailingContent: (@Composable (() -> Unit))? = null,
    onClick: () -> Unit
) {
    when (variant) {
        BBQButtonVariant.PRIMARY -> FilledButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            background = MaterialTheme.extendedColors.primary,
            contentColor = MaterialTheme.extendedColors.primaryForeground,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            onClick = onClick
        )
        BBQButtonVariant.SECONDARY -> FilledButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            background = MaterialTheme.extendedColors.secondary,
            contentColor = MaterialTheme.extendedColors.secondaryForeground,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            onClick = onClick
        )
        BBQButtonVariant.DESTRUCTIVE -> FilledButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            background = MaterialTheme.extendedColors.destructive,
            contentColor = MaterialTheme.extendedColors.destructiveForeground,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            onClick = onClick
        )
        BBQButtonVariant.OUTLINE -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            border = BorderStroke(1.dp, MaterialTheme.extendedColors.border),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            ButtonContent(text, leadingContent, trailingContent)
        }
        BBQButtonVariant.GHOST -> ElevatedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
        ) {
            ButtonContent(text, leadingContent, trailingContent)
        }
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
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        )
        trailingContent?.invoke()
    }
}
