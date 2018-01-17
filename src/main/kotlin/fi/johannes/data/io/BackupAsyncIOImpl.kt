package fi.johannes.data.io

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.function.BiPredicate
import java.util.stream.Collectors
import java.nio.file.attribute.BasicFileAttributes



/**
 * Johannes on 15.1.2018.
 */
class BackupAsyncIOImpl(val directory: String,
                        val fileformat: String) : BackupAsyncIO {

    fun <T> Optional<T>.orNull(): T? = orElse(null)

    private fun writeFile(title: String, filename: String, content: String) {
        val p = Paths.get(directory, title, "$filename.$fileformat")
        Files.write(p, content.toByteArray(Charset.defaultCharset()), StandardOpenOption.CREATE_NEW)
    }

    private fun getFiles(title: String): List<Path> {
        val dir = Paths.get(directory, title)
        return Files.walk(dir, 0).collect(Collectors.toList())
    }

    private fun getFile(title: String, filename:String): Path? {
        val dir = Paths.get(directory, title)
        return Files.find(dir, 0, BiPredicate { p, attr ->
            p.fileName.toString().equals(filename)
        }).findFirst().orNull()
    }

    private fun getFilesSorted(title: String): List<Path> {
        val dir = Paths.get(directory, title)
        return Files.walk(dir, 0).sorted { one, other ->
            Files.getLastModifiedTime(other).compareTo(Files.getLastModifiedTime(one))
        }.collect(Collectors.toList())
    }


    private fun getLatestFile(title: String): Path? {
        return getFilesSorted(title).firstOrNull()
    }

    override fun saveBackup(title: String, content:String, ready: Handler<AsyncResult<Unit>>) {
        val filename = ""
        val result = {
            val u = writeFile(title, filename, content)
            Future.succeededFuture(u)
        }
        ready.handle(result())
    }

    override fun getBackupForName(title: String, filename: String, ready: Handler<AsyncResult<Path?>>) {
        val result = {
            val f = getFile(title, filename)
            Future.succeededFuture(f)
        }
        ready.handle(result())
    }

    override fun getBackups(title: String, ready: Handler<AsyncResult<List<Path>>>) {
        val result = {
            val fs = getFiles(title)
            Future.succeededFuture(fs)
        }
        ready.handle(result())
    }
    override fun pruneBackups(title: String, numberToKeep: Int, ready: Handler<AsyncResult<Unit>>) {
        // todo
        ready.handle(Future.succeededFuture())
    }
}