package fi.johannes.web.handlers

import fi.johannes.data.services.proxy.BackupService

/**
 * Johannes on 14.1.2018.
 */
class RepositoryControllerComponentsImpl(private val backupService: BackupService): RepositoryControllerComponents {

    override fun backupService(): BackupService {
        return backupService
    }
    // todo implement passing messages to saving
}