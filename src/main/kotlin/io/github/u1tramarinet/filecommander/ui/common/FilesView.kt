package io.github.u1tramarinet.filecommander.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.u1tramarinet.filecommander.domain.MyFiles

@Composable
fun FilesView(modifier: Modifier = Modifier) {
    val state = rememberFilesViewState()
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(0.9f).padding(horizontal = 16.dp),
                text = state.currentDirectory?.path ?: "",
            )
            IconButton(modifier = Modifier.weight(0.1f), onClick = { state.navigateParent() }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = null)
            }
        }
        Divider(thickness = 10.dp)
        VerticallyScrollableBox(modifier = modifier) {
            Column {
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
    }
}

@Composable
private fun FileView(modifier: Modifier = Modifier, file: MyFiles.MyFile) {
    ItemBaseView(modifier = modifier) {
        Text(text = file.name)
    }
}

@Composable
private fun DirectoryView(modifier: Modifier = Modifier, directory: MyFiles.MyDirectory, onClick: () -> Unit) {
    ItemBaseView(modifier = modifier.clickable { onClick() }) {
        Icon(imageVector = Icons.Outlined.Menu, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = directory.name)
    }
}

@Composable
private fun ItemBaseView(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
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
            currentChildren = (currentDirectory?.children ?: emptyList()).sortedWith(
                compareBy<MyFiles> { it is MyFiles.MyFile }.thenBy { it.name.lowercase() }
            ),
            navigateParent = {
                currentDirectory = currentDirectory?.parent
            },
            navigate = { directory ->
                currentDirectory = directory
            }
        )
    }
}