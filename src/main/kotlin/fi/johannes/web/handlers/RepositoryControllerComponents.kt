package fi.johannes.web.handlers

import fi.johannes.data.services.proxy.BackupService

/**
 * Johannes on 14.1.2018.
 */
interface RepositoryControllerComponents {

    fun backupService(): BackupService
}