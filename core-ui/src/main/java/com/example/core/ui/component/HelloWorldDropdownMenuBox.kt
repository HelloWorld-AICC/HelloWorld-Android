package com.example.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.ui.R
import com.example.core.ui.theme.HelloWorldMain100

@Composable
fun <T> HWDropdownMenuBox(
    modifier: Modifier = Modifier,
    selectedItem: T? = null,
    items: List<T> = emptyList(),
    iconPosition: IconPosition = IconPosition.RIGHT,
    iconSize: Dp = 24.dp,
    expanded: Boolean = false,
    onExpandedChange: () -> Unit,
    onClick: (T) -> Unit,
    selectedContent: @Composable (selectedItem: T?) -> Unit,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit,
    hideSelectedItem: Boolean = false,
) {
    val bottomSheetState = remember(expanded) {
        MutableTransitionState(false).apply {
            targetState = expanded
        }
    }

    Column(
        modifier = modifier
            .border(2.dp, HelloWorldMain100, RoundedCornerShape(8.dp))
            .background(Color.White),
    ) {
        Row(
            modifier = modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onExpandedChange() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconPosition == IconPosition.LEFT) {
                Icon(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_keyboard_arrow_down else R.drawable.ic_keyboard_arrow_up
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(iconSize)
                )
            }
            // 선택된 아이템 표시 영역
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedContent(selectedItem)
            }
            if (iconPosition == IconPosition.RIGHT) {
                Icon(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_keyboard_arrow_down else R.drawable.ic_keyboard_arrow_up
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(iconSize)
                )
            }
        }
        if (expanded) {
            AnimatedVisibility(
                visibleState = bottomSheetState,
                modifier = modifier
            ) {
                LazyColumn {
                    val filteredItems = if (hideSelectedItem) {
                        items.filter { it != selectedItem }
                    } else {
                        items
                    }

                    items(filteredItems) { item ->
                        val isSelected = item == selectedItem
                        Row(
                            modifier = modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onClick(item) }
                                .padding(12.dp),
                            horizontalArrangement = if (iconPosition == IconPosition.LEFT) {
                                Arrangement.End
                            } else {
                                Arrangement.Start
                            }
                        ) {
                            itemContent(item, isSelected)
                        }
                    }
                }
            }
        }
    }
}

enum class IconPosition {
    LEFT,
    RIGHT
}