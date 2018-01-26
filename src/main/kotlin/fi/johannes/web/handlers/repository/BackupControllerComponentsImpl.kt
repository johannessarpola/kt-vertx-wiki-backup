package fi.johannes.web.handlers.repository

import fi.johannes.data.services.proxy.BackupService

/**
 * Johannes on 14.1.2018.
 */
class BackupControllerComponentsImpl(private val backupService: BackupService): BackupControllerComponents {

    override fun backupService(): BackupService {
        return backupService
    }
    // todo implement passing messages to saving
}