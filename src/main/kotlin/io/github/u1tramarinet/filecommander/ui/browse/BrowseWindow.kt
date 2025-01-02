package io.github.u1tramarinet.ui.browse

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import io.github.u1tramarinet.ui.common.FilesView
import io.github.u1tramarinet.ui.common.WindowScaffold

@Composable
fun ApplicationScope.BrowseWindow(
    onCloseRequest: () -> Unit,
) {
    WindowScaffold(
        title = "Browse Files",
        onCloseRequest = onCloseRequest,
        resizable = true,
    ) {
        FilesView()
    }
}