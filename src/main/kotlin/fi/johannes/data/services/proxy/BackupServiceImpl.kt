package fi.johannes.data.services.proxy

import fi.johannes.data.dao.BackupDao
import fi.johannes.data.enums.ErrorCodes
import fi.johannes.data.io.BackupAsyncIO
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

/**
 * Johannes on 14.1.2018.
 */
class BackupServiceImpl(private val bfs: BackupAsyncIO,
                        private val dao: BackupDao,
                        val readyHandler: Handler<AsyncResult<BackupService>>): BackupService {

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


    override fun fetchPage(title: String, resultHandler: Handler<AsyncResult<JsonObject>>): BackupService {
        val params = JsonArray().add(title)

        dao.latest(params,
                success = { result ->
                    val response = JsonObject()
                    if (result.getNumRows() == 0) {
                        response.put("found", false)
                    } else {
                        response.put("found", true)
                        val row = result.getResults().get(0)
                        response.put("id", row.getInteger(0))
                                .put("title", row.getString(1))
                                .put("file", row.getString(2))
                    }
                    resultHandler.handle(Future.succeededFuture(response))
                },
                error = { error ->
                    LOGGER.error("Database query error", error)
                    resultHandler.handle(Future.failedFuture(error))
                })
        return this
    }


    override fun savePage(title: String, markdown: String, resultHandler: Handler<AsyncResult<JsonObject>>): BackupService {
        // title, filename, date
        val params = JsonArray().add(title).add(markdown) // todo
        // todo need a proper way to configure file store here
        dao.save(params,
                success = { ->
                    val response = JsonObject()
                    resultHandler.handle(Future.succeededFuture(response))
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
