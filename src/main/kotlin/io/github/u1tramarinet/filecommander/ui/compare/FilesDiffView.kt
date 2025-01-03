package io.github.u1tramarinet.filecommander.ui.compare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.u1tramarinet.filecommander.domain.MyFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@Composable
fun FilesDiffView(modifier: Modifier = Modifier) {
    val state = rememberFilesDiffViewState()
    Column(modifier = modifier) {
        ItemRowView(
            pathColumn = {},
            beforeColumn = {
                Text(text = "${state.beforeUpdateTime ?: ""}")
                Button(onClick = { state.updateBefore() }) {
                    Text(text = "更新")
                }
            },
            afterColumn = {
                Text(text = "${state.afterUpdateTime ?: ""}")
                Button(onClick = { state.updateAfter() }) {
                    Text(text = "更新")
                }
            },
        )
        state.diffs.forEach { entry ->
            ItemRowView(
                pathColumn = {
                    Text(text = entry.key)
                },
                beforeColumn = {
                    Text(text = "${entry.value}")
                },
                afterColumn = {
                    Text(text = "${entry.value}")
                },
            )
        }
    }
}

@Composable
private fun ItemRowView(
    modifier: Modifier = Modifier,
    pathColumn: @Composable RowScope.() -> Unit,
    beforeColumn: @Composable RowScope.() -> Unit,
    afterColumn: @Composable RowScope.() -> Unit,
) {
    Row(modifier = modifier) {
        Row(modifier = Modifier.weight(0.55f), content = pathColumn)
        Row(modifier = Modifier.weight(0.2f), content = beforeColumn)
        Row(modifier = Modifier.weight(0.2f), content = afterColumn)
    }
}

private data class FilesDiffViewState(
    val beforeUpdateTime: LocalDateTime? = null,
    val afterUpdateTime: LocalDateTime? = null,
    val diffs: Map<String, Pair<Long?, Long?>> = emptyMap(),
    val updateBefore: () -> Unit,
    val updateAfter: () -> Unit,
)

@Composable
private fun rememberFilesDiffViewState(): FilesDiffViewState {
    val currentDirectory by remember { mutableStateOf(MyFiles.createRoot()) }
    var beforeUpdateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var afterUpdateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    val beforeFiles = remember { mutableStateMapOf<String, Long?>() }
    LaunchedEffect(beforeUpdateTime) {
        if (beforeUpdateTime != null) {
            println("Update before map")
            withContext(Dispatchers.Default) {
                val files = currentDirectory?.walkFilesAsync { file ->
                    println("file: ${file.path}")
                    file.space
                }
                beforeFiles.clear()
                if (files != null) {
                    beforeFiles.putAll(files)
                }
                println("Update after map: size=${beforeFiles.size}")
            }
        }
    }
    val afterFiles = remember { mutableStateMapOf<String, Long?>() }
    LaunchedEffect(afterUpdateTime) {
        if (afterUpdateTime != null) {
            println("Update after map")
            withContext(Dispatchers.Default) {
                val files = currentDirectory?.walkFilesAsync { file ->
                    println("file: ${file.path}")
                    file.space
                }
                afterFiles.clear()
                if (files != null) {
                    afterFiles.putAll(files)
                }
                println("Update after map: size=${afterFiles.size}")
            }
        }
    }
    val diffs = remember { mutableStateMapOf<String, Pair<Long?, Long?>>() }
    LaunchedEffect(beforeFiles.size, afterFiles.size) {
        println("Update diff map: before=${beforeFiles.size}, after=${afterFiles.size}")
        val map: MutableMap<String, Pair<Long?, Long?>> = beforeFiles.map { entry ->
            entry.key to Pair(entry.value, null)
        }.toMap().toMutableMap()
        afterFiles.forEach { entry ->
            val file = map[entry.key]
            map[entry.key] = if (file != null) {
                Pair(file.first, entry.value)
            } else {
                Pair(null, entry.value)
            }
        }
        println("Update diff map: size=${map.size}")
        diffs.clear()
        diffs.putAll(map)
    }

    return remember(diffs) {
        FilesDiffViewState(
            beforeUpdateTime = beforeUpdateTime,
            afterUpdateTime = afterUpdateTime,
            diffs = diffs,
            updateBefore = {
                beforeUpdateTime = LocalDateTime.now()
                diffs.clear()
            },
            updateAfter = {
                afterUpdateTime = LocalDateTime.now()
                diffs.clear()
            },
        )
    }
}