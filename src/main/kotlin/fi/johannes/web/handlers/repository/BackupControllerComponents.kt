package fi.johannes.web.handlers.repository

import fi.johannes.data.services.proxy.BackupService

/**
 * Johannes on 14.1.2018.
 */
interface BackupControllerComponents {

    fun backupService(): BackupService
}