package fi.johannes.data

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.logging.Logger

/**
 * Johannes on 14.1.2018.
 */
class BackupVerticle:AbstractVerticle() {

    companion object {
        val CONFIG_FILE_STORE = "backup.file-store"
        val CONFIG_FILE_STORE_FORMAT = "backup.file-format"
        val CONFIG_BACKUP_QUEUE = "backup.queue"
    }

    private val logger: Logger  by lazy {
        io.vertx.core.logging.LoggerFactory.getLogger(this::class.java)
    }

    override fun start(startFuture: Future<Void>?) {
        super.start(startFuture)
    }

    override fun stop(stopFuture: Future<Void>?) {
        super.stop(stopFuture)
    }

    // todo save to disk
}