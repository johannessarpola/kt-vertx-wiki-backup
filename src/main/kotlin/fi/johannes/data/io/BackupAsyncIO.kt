package fi.johannes.data.io

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import java.nio.file.Path

/**
 * Johannes on 15.1.2018.
 */
interface BackupAsyncIO {
    fun getBackups(title: String, ready: Handler<AsyncResult<List<Path>>>)
    fun pruneBackups(title: String, numberToKeep: Int, ready: Handler<AsyncResult<Void>>)
    fun saveBackup(title: String, content: String, ready: Handler<AsyncResult<Void>>)
}