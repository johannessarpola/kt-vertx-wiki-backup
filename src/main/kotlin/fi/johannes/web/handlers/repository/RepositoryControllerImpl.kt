package fi.johannes.web.handlers.repository

import fi.johannes.web.handlers.RepositoryControllerComponents
import fi.johannes.web.utils.RequestUtils.getParam
import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

/**
 * Johannes on 14.1.2018.
 */
class RepositoryControllerImpl(private val components: RepositoryControllerComponents): RepositoryController {
    override fun save(context: RoutingContext) {
        TODO("Not implemented")
    }

    override fun get(context: RoutingContext) {
        val title = getParam("title", context.request());
        components.backupService().fetchPage(title, Handler { ar ->
            if(ar.succeeded()) {
                val result = ar.result()
                val response = when(result.getBoolean("found")) {
                    false -> JsonObject().put("found", false)
                    true -> JsonObject().put("found", true) // todo handle found
                }
                context.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(response.encode())
            }
            else {
                context.fail(ar.cause())
            }
        })
    }
}