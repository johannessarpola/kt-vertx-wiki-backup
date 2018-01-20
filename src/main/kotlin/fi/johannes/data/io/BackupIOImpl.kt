package fi.johannes.data.io

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.function.BiPredicate
import java.util.stream.Collectors
import org.funktionale.composition.*

/**
 * Johannes on 15.1.2018.
 */
class BackupIOImpl(val directory: String,
                   val fileformat: String) : BackupIO {


    fun <T> Optional<T>.orNull(): T? = orElse(null)

    init {
        Files.createDirectories(Paths.get(directory))
    }

    private fun writeFile(title: String, filename: String, content: String) {
        val dir = Paths.get(directory, title)
        val p = Paths.get(directory, title, "$filename.$fileformat")
        Files.createDirectories(dir)
        Files.write(p, content.toByteArray(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW)
    }

    private fun getFiles(title: String): List<Path> {
        val dir = Paths.get(directory, title)
        return Files.walk(dir, 0).collect(Collectors.toList())
    }

    private fun getFile(title: String, filename: String): Path? {
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

    override fun saveBackup(title: String, filename: String, content: String, ready: Handler<AsyncResult<Unit>>) {
        val result = {
            val u = writeFile(title, filename, content)
            Future.succeededFuture(u)
        }
        ready.handle(result())
    }

    override fun getBackupPath(title: String, backupName: String, ready: Handler<AsyncResult<Path?>>) {
        val result = {
            val f = getFile(title, backupName)
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

    override fun getBackupContent(title: String, backupName: String, ready: Handler<AsyncResult<String?>>) {
        val result = { getFile(title, backupName) } andThen { path ->
            if(path != null) {
                val encoded = Files.readAllBytes(path)
                Future.succeededFuture(String(encoded, StandardCharsets.UTF_8))
            }
            else {
                Future.failedFuture(IOException("No file found"))
            }
        }
        return ready.handle(result())
    }
}