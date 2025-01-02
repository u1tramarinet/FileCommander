package io.github.u1tramarinet

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onOkClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    DialogWindow(
        onCloseRequest = onCancelClick,
        title = title,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = message)
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onOkClick) {
                    Text(text = "Ok")
                }
                Spacer(modifier = Modifier.width(32.dp))
                Button(onClick = onCancelClick) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@Composable
@Preview
private fun ConfirmationDialogPreview() {
    ConfirmationDialog(title = "title", message = "message", onOkClick = {}, onCancelClick = {})
}