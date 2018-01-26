package fi.johannes.web.handlers.status

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import fi.johannes.web.handlers.status.controllers.StatusController
import fi.johannes.web.handlers.status.controllers.StatusControllerImpl

/**
 * Johannes on 26.1.2018.
 */
class StatusControllers() {

    val injector = Kodein {
        bind<StatusController>() with singleton { StatusControllerImpl(instance()) }
    }

}