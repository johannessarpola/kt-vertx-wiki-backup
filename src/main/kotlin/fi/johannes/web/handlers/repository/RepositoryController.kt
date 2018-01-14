package fi.johannes.web.handlers.repository

import io.vertx.ext.web.RoutingContext

/**
 * Johannes on 14.1.2018.
 */
interface RepositoryController {
    /**
     * Saves using id if updating otherwise creates new
     */
    fun save(context: RoutingContext)

    /**
     * Saves using id if updating otherwise creates new
     */
    fun get(context: RoutingContext)

}