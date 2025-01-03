package io.github.u1tramarinet.filecommander.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

sealed interface MyFiles {
    val name: String
    val path: String
    val parent: MyDirectory?
    val isHidden: Boolean
    val space: Long

    class MyFile private constructor(pathName: String) : MyFiles {
        private val file: File = File(pathName)
        override val path: String
            get() = file.path
        override val name: String
            get() = file.nameWithoutExtension
        override val parent: MyDirectory?
            get() = getParent(path)
        override val isHidden: Boolean
            get() = file.isHidden
        override val space: Long
            get() = getFileSize(this)
        val extension: String
            get() = file.extension

        init {
            if (!file.isFile) {
                throw IllegalArgumentException()
            }
        }

        companion object {
            fun create(pathName: String): MyFile? {
                return try {
                    MyFile(pathName = pathName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    class MyDirectory private constructor(
        pathName: String,
        private val forceVisible: Boolean = false,
    ) : MyFiles {
        private val file = File(pathName)
        override val path: String
            get() = file.path
        override val name: String
            get() = file.name.ifEmpty { file.path }
        override val parent: MyDirectory?
            get() = getParent(path)
        override val isHidden: Boolean
            get() = file.isHidden && !forceVisible
        override val space: Long
            get() = getDirectorySize(this)
        val children: List<MyFiles>
            get() = getChildren(this)

        init {
            if (!file.isDirectory) {
                throw IllegalArgumentException()
            }
        }

        fun walkFiles(visit: (MyFile) -> Unit) {
            walkFiles(this, visit)
        }

        fun <T> walkFilesAsync(visit: (MyFile) -> T): Map<String, T> {
            println("walkFilesAsync() start")
            val result = walkFilesAsync(this, visit)
            println("walkFilesAsync() end(size=${result.size})")
            return result
        }

        companion object {
            fun create(pathName: String): MyDirectory? {
                return try {
                    MyDirectory(pathName = pathName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    companion object {
        private val ROOT_DIR = File.separator

        fun create(pathName: String): MyFiles? {
            val file = File(pathName)
            return try {
                if (file.isFile) {
                    MyFile.create(pathName)
                } else if (file.isDirectory) {
                    MyDirectory.create(pathName)
                } else {
                    null
                }
            } catch (e: SecurityException) {
                null
            }
        }

        fun createRoot(): MyDirectory? {
            val roots = File.listRoots()
            val rootPathName = if (roots.size <= 1) {
                roots.firstOrNull()?.path
            } else {
                ROOT_DIR
            }
            return rootPathName?.let { MyDirectory.create(rootPathName) }
        }

        private fun getParent(path: String): MyDirectory? {
            val parentPath = if (path == ROOT_DIR) {
                null
            } else {
                val origin = File(path).parent
                if (origin != null) {
                    origin
                } else {
                    val roots = File.listRoots()
                    if (roots.size > 1 && roots.any { it.path == path }) {
                        ROOT_DIR
                    } else {
                        null
                    }
                }
            }
            return parentPath?.let { MyDirectory.create(parentPath) }
        }

        private fun getChildren(directory: MyDirectory): List<MyFiles> {
            return if (directory.path == ROOT_DIR) {
                File.listRoots().mapNotNull { root ->
                    create(root.path)
                }
            } else {
                File(directory.path).listFiles()?.mapNotNull { file ->
                    create(file.path)
                } ?: emptyList()
            }
        }

        private fun getDirectorySize(directory: MyDirectory): Long {
            return if (directory.path == ROOT_DIR) {
                File.listRoots().fold(0L) { size, root ->
                    size + root.getUsedSpace()
                }
            } else {
                val matchedRoot = File.listRoots().firstOrNull { it.path == directory.path }
                matchedRoot?.getUsedSpace()
                    ?: directory.children.fold(0L) { total, child ->
                        total + when (child) {
                            is MyDirectory -> {
                                getDirectorySize(child)
                            }

                            is MyFile -> {
                                child.space
                            }
                        }
                    }
            }
        }

        private fun walkFiles(directory: MyDirectory, visit: (file: MyFile) -> Unit) {
            return directory.children.forEach { child ->
                when (child) {
                    is MyDirectory -> {
                        walkFiles(child, visit)
                    }

                    is MyFile -> {
                        visit(child)
                    }
                }
            }
        }

        private fun <T> walkFilesAsync(directory: MyDirectory, visit: (file: MyFile) -> T): Map<String, T> {
            println("walkFilesAsync(directory=${directory.path}): start")
            val map = mutableMapOf<String, T>()
            runBlocking {
                directory.children.mapNotNull { child ->
                    when (child) {
                        is MyDirectory -> {
                            // NOP
                            null
                        }

                        is MyFile -> {
                            async(Dispatchers.IO) {
                                println("file: ${child.path}")
                                val result = visit(child)
                                map[child.path] = result
                            }
                        }
                    }
                }.awaitAll()
            }
            directory.children.mapNotNull { child ->
                when (child) {
                    is MyDirectory -> {
                        val results = walkFilesAsync(child, visit)
                        map.putAll(results)
                    }

                    is MyFile -> {
                        // NOP
                        null
                    }
                }
            }
            println("walkFilesAsync(directory=${directory.path}): end")
            return map
        }

        private fun getFileSize(file: MyFile): Long {
            val path = Paths.get(file.path)
            return Files.size(path)
        }

        private fun File.getUsedSpace() = totalSpace - usableSpace
    }
}