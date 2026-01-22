package com.cwalcott.mylibrary.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cwalcott.mylibrary.R
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme

@Composable
fun ContentUnavailableView(
    modifier: Modifier = Modifier,
    iconPainter: Painter? = null,
    headlineText: String? = null,
    subheadlineText: String? = null,
    bottomContent: @Composable (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp)
            )
        }

        if (headlineText != null) {
            Text(
                text = headlineText,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (subheadlineText != null) {
            Text(
                text = subheadlineText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (bottomContent != null) {
            bottomContent()
        }
    }
}

@Preview
@Composable
fun ContentUnavailableViewPreview() {
    MyLibraryTheme {
        ContentUnavailableView(
            iconPainter = painterResource(id = R.drawable.star),
            headlineText = "Title",
            subheadlineText = "Subheadline Text"
        )
    }
}
