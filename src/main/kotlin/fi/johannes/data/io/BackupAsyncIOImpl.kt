package fi.johannes.data.io

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

/**
 * Johannes on 15.1.2018.
 */
class BackupAsyncIOImpl(val directory: String,
                        val fileformat: String) : BackupAsyncIO {

    private fun writeFile(title: String, filename: String, content: String) {
        // title is folder
        val p = Paths.get(directory, title, "$filename.$fileformat")
        Files.write(p, content.toByteArray(Charset.defaultCharset()), StandardOpenOption.CREATE_NEW)
    }
    override fun saveBackup(title: String, content:String, ready: Handler<AsyncResult<Void>>) {
        // todo
        ready.handle(Future.succeededFuture())
    }
    override fun getBackups(title: String, ready: Handler<AsyncResult<List<Path>>>) {
        // todo
        ready.handle(Future.succeededFuture(Collections.emptyList()))
    }
    override fun pruneBackups(title: String, numberToKeep: Int, ready: Handler<AsyncResult<Void>>) {
        // todo
        ready.handle(Future.succeededFuture())
    }
}