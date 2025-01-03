package io.github.u1tramarinet.filecommander.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import io.github.u1tramarinet.filecommander.ui.common.WindowScaffold

@Composable
fun ApplicationScope.MainWindow(
    onCloseRequest: () -> Unit,
    onClickBrowse: () -> Unit,
    onClickCompare: () -> Unit,
) {
    WindowScaffold(
        title = "FileCommander",
        onCloseRequest = onCloseRequest,
        resizable = true,
        menuBarContent = {
            Menu("File") {
                Item("Exit", onClick = onCloseRequest)
            }
        }
    ) {
        MainScreen(onClickBrowse = onClickBrowse, onClickCompare = onClickCompare)
    }
}