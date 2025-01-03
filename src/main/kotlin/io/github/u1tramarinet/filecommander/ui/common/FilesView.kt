package io.github.u1tramarinet.filecommander.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.u1tramarinet.filecommander.domain.MyFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@Composable
fun FilesView(modifier: Modifier = Modifier) {
    val state = rememberFilesViewState()
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(0.8f).padding(horizontal = 16.dp),
                text = state.currentDirectory?.path ?: "",
            )
            IconButton(modifier = Modifier.weight(0.1f), onClick = { state.navigateParent() }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = null)
            }
            IconButton(modifier = Modifier.weight(0.1f), onClick = { state.update() }) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
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
    ItemBaseView(
        modifier = modifier,
        optionContent = {
            FileSizeView(file = file)
        },
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle()) {
                    append(file.name)
                }
                withStyle(SpanStyle(color = Color.Gray)) {
                    append(".${file.extension}")
                }
            },
            fontSize = 24.sp,
        )
    }
}

@Composable
private fun DirectoryView(modifier: Modifier = Modifier, directory: MyFiles.MyDirectory, onClick: () -> Unit) {
    ItemBaseView(
        modifier = modifier.clickable { onClick() },
        optionContent = {
            FileSizeView(file = directory)
        },
    ) {
        Icon(modifier = Modifier.weight(0.1f), imageVector = Icons.Outlined.Menu, contentDescription = null)
        Text(modifier = Modifier.weight(0.9f), text = directory.name, fontSize = 24.sp)
    }
}

@Composable
private fun FileSizeView(
    modifier: Modifier = Modifier,
    file: MyFiles,
) {
    var size by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(file) {
        size = withContext(Dispatchers.IO) {
            file.space
        }
    }
    val text = if (size != null) {
        val bytes = size!!
        when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    } else {
        "計算中..."
    }
    Text(modifier = modifier, text = text, fontSize = 16.sp, color = Color.Gray)
}

@Composable
private fun ItemBaseView(
    modifier: Modifier = Modifier,
    optionContent: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(0.6f),
            content = content,
            verticalAlignment = Alignment.CenterVertically,
        )
        Row(
            modifier = Modifier.weight(0.4f),
            content = optionContent,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        )
    }
}

private data class FilesViewState(
    val currentDirectory: MyFiles.MyDirectory?,
    val currentChildren: List<MyFiles>,
    val navigateParent: () -> Unit,
    val navigate: (directory: MyFiles.MyDirectory) -> Unit,
    val update: () -> Unit,
)

@Composable
private fun rememberFilesViewState(): FilesViewState {
    var currentDirectory by remember { mutableStateOf(MyFiles.MyDirectory.create(System.getProperty("user.home"))) }
    var updateTime by remember { mutableStateOf(LocalDateTime.now()) }
    val currentChildren by remember(currentDirectory, updateTime) {
        mutableStateOf(
            (currentDirectory?.children ?: emptyList()).sortedWith(
                compareBy<MyFiles> { it is MyFiles.MyFile }.thenBy { it.name.lowercase() }
            ))
    }
    return remember(currentDirectory, currentChildren) {
        FilesViewState(
            currentDirectory = currentDirectory,
            currentChildren = currentChildren,
            navigateParent = {
                currentDirectory = currentDirectory?.parent
            },
            navigate = { directory ->
                currentDirectory = directory
            },
            update = {
                updateTime = LocalDateTime.now()
            }
        )
    }
}