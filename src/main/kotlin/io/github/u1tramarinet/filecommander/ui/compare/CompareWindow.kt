package io.github.u1tramarinet.filecommander.ui.compare

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ApplicationScope
import io.github.u1tramarinet.filecommander.ui.common.FilesView
import io.github.u1tramarinet.filecommander.ui.common.WindowScaffold

@Composable
fun ApplicationScope.CompareWindow(
    onCloseRequest: () -> Unit,
) {
    WindowScaffold(
        title = "Compare Files",
        onCloseRequest = onCloseRequest,
        resizable = true,
    ) {
        FilesDiffView(modifier = Modifier.fillMaxSize())
    }
}