package io.github.u1tramarinet.filecommander.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope

@Composable
fun FrameWindowScope.MainScreen(
    modifier: Modifier = Modifier,
    onClickBrowse: () -> Unit,
    onClickCompare: () -> Unit,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Button(modifier = Modifier.fillMaxWidth(), onClick = onClickBrowse) {
            Text("Browse Files")
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = onClickCompare) {
            Text("Compare Files")
        }
    }
}