package fi.johannes.web.handlers.status

import io.vertx.ext.web.RoutingContext

/**
 * Johannes on 14.1.2018.
 */
interface StatusController {

    fun get(context: RoutingContext)
}