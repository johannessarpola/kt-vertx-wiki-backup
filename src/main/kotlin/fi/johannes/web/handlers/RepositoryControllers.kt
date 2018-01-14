package fi.johannes.web.handlers

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import fi.johannes.web.handlers.repository.RepositoryController
import fi.johannes.web.handlers.repository.RepositoryControllerImpl
import io.vertx.core.eventbus.EventBus

/**
 * Johannes on 14.1.2018.
 */
class RepositoryControllers(val backupQueueAddress: String,
                            val backupEventBus: EventBus) {

    val injector = Kodein {
        bind<RepositoryControllerComponents>() with singleton {
            RepositoryControllerComponentsImpl(backupQueueAddress, backupEventBus)
        }
        bind<RepositoryController>() with singleton { RepositoryControllerImpl(instance()) }
    }

}