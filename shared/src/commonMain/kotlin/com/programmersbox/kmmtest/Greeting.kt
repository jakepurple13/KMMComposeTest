package com.programmersbox.kmmtest

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Greeting {
    private val platform: Platform = getPlatform()

    fun greeting(): String {
        return "Hello, ${platform.name}!"
    }
}

class GroupButtonModel<T>(val item: T, val iconContent: @Composable () -> Unit)

@Composable
fun <T> GroupButton(
    selected: T,
    options: List<GroupButtonModel<T>>,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colors.primary,
    unselectedColor: Color = MaterialTheme.colors.surface,
    selectedContentColor: Color = MaterialTheme.colors.onPrimary,
    unselectedContentColor: Color = MaterialTheme.colors.onSurface,
    shape: CornerBasedShape = RoundedCornerShape(20.0.dp),
    onClick: (T) -> Unit
) {
    Row(modifier) {
        val noCorner = CornerSize(0.dp)

        options.forEachIndexed { i, option ->
            OutlinedButton(
                modifier = Modifier,
                onClick = { onClick(option.item) },
                shape = shape.copy(
                    topStart = if (i == 0) shape.topStart else noCorner,
                    topEnd = if (i == options.size - 1) shape.topEnd else noCorner,
                    bottomStart = if (i == 0) shape.bottomStart else noCorner,
                    bottomEnd = if (i == options.size - 1) shape.bottomEnd else noCorner
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = animateColorAsState(if (selected == option.item) selectedColor else unselectedColor).value,
                    contentColor = animateColorAsState(if (selected == option.item) selectedContentColor else unselectedContentColor).value
                )
            ) { option.iconContent() }
        }
    }
}