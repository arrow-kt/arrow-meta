package arrow.meta.ide.testing.env.github

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets


// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
object Executor {
  internal val LOG = Logger.getInstance(Executor::class.java)
  private var ourCurrentDir: String = ""
  private fun cdAbs(absolutePath: String) {
    ourCurrentDir = absolutePath
    LOG.info("# cd " + shortenPath(absolutePath))
  }

  private fun cdRel(relativePath: String) {
    cdAbs("$ourCurrentDir/$relativePath")
  }

  fun cd(dir: File) {
    cdAbs(dir.absolutePath)
  }

  fun cd(relativeOrAbsolutePath: String) {
    if (relativeOrAbsolutePath.startsWith("/") || relativeOrAbsolutePath[1] == ':') {
      cdAbs(relativeOrAbsolutePath)
    } else {
      cdRel(relativeOrAbsolutePath)
    }
  }

  fun cd(dir: VirtualFile) {
    cd(dir.path)
  }

  fun pwd(): String = ourCurrentDir

  fun touch(filePath: String): File {
    return try {
      val file: File = child(filePath)
      assert(!file.exists()) { "File $file shouldn't exist yet" }
      File(file.parent).mkdirs() // ensure to create the directories
      val fileCreated = file.createNewFile()
      assert(fileCreated)
      LOG.info("# touch $filePath")
      file
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  fun touch(fileName: String, content: String): File {
    val filePath: File = touch(fileName)
    echo(fileName, content)
    return filePath
  }

  fun echo(fileName: String, content: String) {
    try {
      FileUtil.writeToFile(child(fileName), content.toByteArray(StandardCharsets.UTF_8), true)
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  @Throws(IOException::class)
  fun overwrite(fileName: String, content: String) {
    overwrite(child(fileName), content)
  }

  @Throws(IOException::class)
  fun overwrite(file: File, content: String) {
    FileUtil.writeToFile(file, content.toByteArray(StandardCharsets.UTF_8), false)
  }

  @Throws(IOException::class)
  fun append(file: File, content: String) {
    FileUtil.writeToFile(file, content.toByteArray(StandardCharsets.UTF_8), true)
  }

  @Throws(IOException::class)
  fun append(fileName: String, content: String) {
    append(child(fileName), content)
  }

  fun rm(fileName: String) {
    rm(child(fileName))
  }

  fun rm(file: File) {
    FileUtil.delete(file)
  }

  fun mkdir(dirName: String): File {
    val file: File = child(dirName)
    val dirMade = file.mkdir()
    LOG.assertTrue(dirMade, "Directory " + dirName + " was not created on [" + file.path + "]. " +
      "list of files in the parent dir: " + file.parentFile.listFiles())
    LOG.info("# mkdir $dirName")
    LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
    return file
  }

  fun cat(fileName: String): String {
    return try {
      val content = FileUtil.loadFile(child(fileName))
      LOG.info("# cat $fileName")
      content
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  fun cp(fileName: String, destinationDir: File) {
    try {
      FileUtil.copy(child(fileName), File(destinationDir, fileName))
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  fun splitCommandInParameters(command: String): List<String> {
    val split: MutableList<String> = ArrayList()
    var insideParam = false
    var currentParam = StringBuilder()
    for (c in command.toCharArray()) {
      var flush = false
      if (insideParam) {
        if (c == '\'') {
          insideParam = false
          flush = true
        } else {
          currentParam.append(c)
        }
      } else if (c == '\'') {
        insideParam = true
      } else if (c == ' ') {
        flush = true
      } else {
        currentParam.append(c)
      }
      if (flush) {
        if (!StringUtil.isEmptyOrSpaces(currentParam.toString())) {
          split.add(currentParam.toString())
        }
        currentParam = StringBuilder()
      }
    }

    // last flush
    if (!StringUtil.isEmptyOrSpaces(currentParam.toString())) {
      split.add(currentParam.toString())
    }
    return split
  }

  private fun shortenPath(path: String): String {
    val split = path.split("/".toRegex()).toTypedArray()
    return if (split.size > 3) {
      // split[0] is empty, because the path starts from /
      String.format("/%s/.../%s/%s", split[1], split[split.size - 2], split[split.size - 1])
    } else path
  }

  fun child(fileName: String): File =
    File(ourCurrentDir, fileName)

  fun ourCurrentDir(): File =
    File(ourCurrentDir)
}