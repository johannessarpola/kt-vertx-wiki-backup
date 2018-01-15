package fi.johannes.data.services.proxy

import io.vertx.codegen.annotations.Fluent
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

/**
 * Johannes on 14.1.2018.
 */
@ProxyGen
interface BackupService {

    @Fluent
    fun fetchPage(title: String, resultHandler: Handler<AsyncResult<JsonObject>>): BackupService

    fun savePage(title: String, markdown: String, resultHandler: Handler<AsyncResult<JsonObject>>): BackupService
}