package fi.johannes.data.services.proxy

import fi.johannes.data.dao.BackupDAO
import fi.johannes.data.enums.ErrorCodes
import fi.johannes.data.io.BackupIO
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * Johannes on 14.1.2018.
 */
class BackupServiceImpl(private val io: BackupIO,
                        private val dao: BackupDAO,
                        val readyHandler: Handler<AsyncResult<BackupService>>) : BackupService {

    private val FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private val LOGGER = LoggerFactory.getLogger(BackupServiceImpl::class.java)

    init {
        dao.createTable(
                success = { ->
                    readyHandler.handle(Future.succeededFuture(this))
                },
                connectionError = { err ->
                    LOGGER.error("Could not open a database connection", err)
                    readyHandler.handle(Future.failedFuture(err))
                },
                createError = { err ->
                    LOGGER.error("Database preparation error", err)
                    readyHandler.handle(Future.failedFuture(err))
                })
    }

    private fun fetchFailed(resultHandler: Handler<AsyncResult<JsonObject>>) {
        val response = JsonObject()
        response.put("message", "No backup found")
                .put("found", false)
        resultHandler.handle(Future.succeededFuture(response))
    }

    override fun fetchPage(title: String, resultHandler: Handler<AsyncResult<JsonObject>>): BackupService {
        val params = JsonArray().add(title)
        dao.latest(params,
                success = { result ->

                    if (result.numRows == 0) {
                        fetchFailed(resultHandler)
                    } else {
                        val response = JsonObject()
                        val row = result.getResults().get(0)
                        val id = row.getInteger(0)
                        val backupName = row.getString(2)

                        io.getBackupContent(title, backupName, Handler { ar ->
                            if (ar.succeeded()) {
                                response.put("id", id)
                                        .put("title", title)
                                        .put("file", backupName)
                                        .put("content", ar.result())
                                        .put("found", true)
                                resultHandler.handle(Future.succeededFuture(response))
                            } else {
                                fetchFailed(resultHandler)
                            }
                        })
                    }
                },
                error = { error ->
                    LOGGER.error("Database query error", error)
                    resultHandler.handle(Future.failedFuture(error))
                })
        return this
    }


    override fun savePage(title: String, markdown: String, resultHandler: Handler<AsyncResult<JsonObject>>): BackupService {
        // title, filename, date
        val now = LocalDateTime.now()
        val sqlDate = java.sql.Date.valueOf(now.toLocalDate())
        val fn = "${title}_${now.format(FORMAT)}"
        val params = JsonArray().add(title).add(fn).add(sqlDate.toString())
        // todo need a proper way to configure file store here
        dao.save(params,
                success = { ->
                    val response = JsonObject()
                    io.saveBackup(title, fn, markdown, Handler {
                        resultHandler.handle(Future.succeededFuture(response))
                    })
                },
                error = { error ->
                    LOGGER.error("Database query error", error)
                    resultHandler.handle(Future.failedFuture(error))
                })
        return this
    }


    private fun reportQueryError(message: Message<JsonObject>, cause: Throwable) {
        LOGGER.error("Database query error", cause)
        message.fail(ErrorCodes.DB_ERROR.ordinal, cause.message)
    }
}
