package com.bbqreset.ui.design

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.bbqreset.ui.design.system.LocalDSShapes

@Composable
fun BBQInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    helperText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    placeholder: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = MaterialTheme.extendedTypography.bodyLarge,
    shape: Shape = LocalDSShapes.current.chip
) {
    Column(modifier = modifier) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.extendedTypography.labelMedium
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            shape = shape,
            visualTransformation = visualTransformation,
            textStyle = textStyle,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            placeholder = placeholder?.let {
                { Text(text = it, style = MaterialTheme.extendedTypography.bodyMedium) }
            }
        )
        if (!helperText.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = helperText,
                style = MaterialTheme.extendedTypography.labelMedium,
                color = MaterialTheme.extendedColors.mutedForeground
            )
        }
    }
}

@Composable
fun rememberInputState(initial: String = ""): MutableState<String> {
    return remember { mutableStateOf(initial) }
}
