package fi.johannes.web.handlers.repository

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import fi.johannes.data.services.proxy.BackupService
import fi.johannes.web.handlers.repository.controllers.RepositoryController
import fi.johannes.web.handlers.repository.controllers.RepositoryControllerImpl

/**
 * Johannes on 14.1.2018.
 */
class BackupControllers(backupService: BackupService) {

    val injector = Kodein {
        bind<BackupControllerComponents>() with singleton {
            BackupControllerComponentsImpl(backupService)
        }
        bind<RepositoryController>() with singleton { RepositoryControllerImpl(instance()) }
    }

}