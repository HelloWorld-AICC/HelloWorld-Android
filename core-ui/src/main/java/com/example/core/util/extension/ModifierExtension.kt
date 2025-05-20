package com.example.core.util.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.max

fun Modifier.advancedImePadding() = composed {
    var consumePadding by remember { mutableIntStateOf(0) }
    onGloballyPositioned { coordinates ->
        val rootHeight = coordinates.findRootCoordinates().size.height
        val componentBottom = (coordinates.positionInWindow().y + coordinates.size.height).toInt()
        consumePadding = max(0, rootHeight - componentBottom)
    }.consumeWindowInsets(
        PaddingValues(bottom = with(LocalDensity.current) { consumePadding.toDp() })
    ).imePadding()
}