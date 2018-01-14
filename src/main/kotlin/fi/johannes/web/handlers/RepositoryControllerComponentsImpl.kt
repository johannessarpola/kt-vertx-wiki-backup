package fi.johannes.web.handlers

import io.vertx.core.eventbus.EventBus

/**
 * Johannes on 14.1.2018.
 */
class RepositoryControllerComponentsImpl(val queue: String,
                                         val eventBus: EventBus): RepositoryControllerComponents {

    // todo implement passing messages to saving
}