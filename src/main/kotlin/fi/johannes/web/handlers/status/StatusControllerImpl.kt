package fi.johannes.web.handlers.status

import fi.johannes.web.handlers.RepositoryControllerComponents
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.kotlin.core.streams.write

/**
 * Johannes on 14.1.2018.
 */
class StatusControllerImpl(components: RepositoryControllerComponents) : StatusController {
    override fun get(context: RoutingContext) {
        val response = JsonObject()
        response.put("status", "ok")
        context.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(response))
    }
}