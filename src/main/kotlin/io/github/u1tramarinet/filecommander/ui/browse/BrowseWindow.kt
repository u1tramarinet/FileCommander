package io.github.u1tramarinet.filecommander.ui.browse

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import io.github.u1tramarinet.filecommander.ui.common.FilesView
import io.github.u1tramarinet.filecommander.ui.common.WindowScaffold

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