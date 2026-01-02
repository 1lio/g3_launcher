package com.g3.launcher.ui.pane.install

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g3.launcher.model.LocalLanguage
import com.g3.launcher.ui.component.G3Text
import com.g3.launcher.ui.theme.ColorTextGray

@Composable
fun BoxScope.WelcomePane(
    onClickSetup: () -> Unit
) {
    val strings = LocalLanguage.current.strings

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(380.dp)
            .wrapContentHeight()
            .padding(24.dp)
            .align(alignment = Alignment.CenterEnd)
    ) {
        G3Text(
            text = strings.setupAction,
            hoverable = true,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            maxLines = 1,
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null,
                onClick = onClickSetup,
            ),
        )

        G3Text(
            text = strings.setupDescription,
            color = ColorTextGray,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            maxLines = 2,
        )
    }
}
