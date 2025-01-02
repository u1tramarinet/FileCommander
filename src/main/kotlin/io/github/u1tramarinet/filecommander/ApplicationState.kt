package io.github.u1tramarinet.filecommander

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.u1tramarinet.filecommander.ui.WindowState

class ApplicationState(startWindow: WindowState) {
    val windowStack = mutableStateListOf<WindowState>()
    var confirmingToClose by mutableStateOf(false)
        private set

    init {
        windowStack.add(startWindow)
    }

    fun <T : WindowState> openWindow(state: T) {
        windowStack.add(state)
    }

    fun closeWindow() {
        if (windowStack.size == 1) {
            confirmingToClose = true
        } else {
            windowStack.removeLast()
        }
    }

    fun closeApp() {
        confirmingToClose = false
        windowStack.clear()
    }

    fun cancelConfirmationToClose() {
        confirmingToClose = false
    }
}