package io.github.u1tramarinet.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.u1tramarinet.domain.MyFiles

@Composable
fun FilesView(modifier: Modifier = Modifier) {
    val state = rememberFilesViewState()
    Column(modifier = modifier) {
        val itemModifier = Modifier.fillMaxWidth()
        state.currentChildren.forEach { child ->
            when (child) {
                is MyFiles.MyFile -> FileView(modifier = itemModifier, file = child)
                is MyFiles.MyDirectory -> DirectoryView(
                    modifier = itemModifier,
                    directory = child,
                    onClick = { state.navigate(child) },
                )
            }
        }
    }
}

@Composable
private fun FileView(modifier: Modifier = Modifier, file: MyFiles.MyFile) {
    Row(modifier = modifier) {
        Text(text = file.name)
    }
}

@Composable
private fun DirectoryView(modifier: Modifier = Modifier, directory: MyFiles.MyDirectory, onClick: () -> Unit) {
    Row(modifier = modifier.clickable { onClick() }) {
        Text(text = directory.name)
    }
}

private data class FilesViewState(
    val currentDirectory: MyFiles.MyDirectory?,
    val currentChildren: List<MyFiles>,
    val navigateParent: () -> Unit,
    val navigate: (directory: MyFiles.MyDirectory) -> Unit,
)

@Composable
private fun rememberFilesViewState(): FilesViewState {
    var currentDirectory by remember { mutableStateOf(MyFiles.MyDirectory.create(System.getProperty("user.home"))) }
    return remember(currentDirectory) {
        FilesViewState(
            currentDirectory = currentDirectory,
            currentChildren = currentDirectory?.children ?: emptyList(),
            navigateParent = {
                currentDirectory = currentDirectory?.parent
            },
            navigate = { directory ->
                currentDirectory = directory
            }
        )
    }
}