import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.window.application
import io.github.u1tramarinet.filecommander.ApplicationState
import io.github.u1tramarinet.filecommander.ConfirmationDialog
import io.github.u1tramarinet.filecommander.ui.main.MainWindow
import io.github.u1tramarinet.filecommander.ui.WindowState
import io.github.u1tramarinet.filecommander.ui.browse.BrowseWindow
import io.github.u1tramarinet.filecommander.ui.browse.BrowseWindowState
import io.github.u1tramarinet.filecommander.ui.compare.CompareWindow
import io.github.u1tramarinet.filecommander.ui.compare.CompareWindowState

fun main() = application {
    val state = remember { ApplicationState(WindowState()) }
    for (window in state.windowStack) {
        key(window) {
            when (window) {
                is BrowseWindowState -> {
                    BrowseWindow(onCloseRequest = state::closeWindow)
                }

                is CompareWindowState -> {
                    CompareWindow(onCloseRequest = state::closeWindow)
                }

                else -> {
                    MainWindow(
                        onCloseRequest = state::closeWindow,
                        onClickBrowse = {
                            state.openWindow(BrowseWindowState())
                        },
                        onClickCompare = {
                            state.openWindow(CompareWindowState())
                        }
                    )
                }
            }
        }
    }
    if (state.confirmingToClose) {
        ConfirmationDialog(
            title = "アプリの終了",
            message = "アプリを終了しますか？",
            onOkClick = state::closeApp,
            onCancelClick = state::cancelConfirmationToClose,
        )
    }
}
