package io.github.u1tramarinet.domain

import java.io.File

sealed interface MyFiles {
    val name: String
    val path: String
    val parent: MyDirectory?

    class MyFile private constructor(pathName: String) : MyFiles {
        private val file: File = File(pathName)
        override val path: String
            get() = file.path
        override val name: String
            get() = file.nameWithoutExtension.ifEmpty { file.name }
        override val parent: MyDirectory?
            get() = getParent(path)
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

    class MyDirectory private constructor(pathName: String) : MyFiles {
        private val file = File(pathName)
        override val path: String
            get() = file.path
        override val name: String
            get() = File(path).name
        override val parent: MyDirectory?
            get() = getParent(path)
        val children: List<MyFiles>
            get() = getChildren(this)

        init {
            if (!file.isDirectory) {
                throw IllegalArgumentException()
            }
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
        private const val ROOT_DIR = "/"

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
                    MyDirectory.create(root.path)
                }
            } else {
                File(directory.path).listFiles()?.mapNotNull { file ->
                    create(file.path)
                } ?: emptyList()
            }
        }
    }
}