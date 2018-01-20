package fi.johannes.data.services.proxy

import fi.johannes.data.dao.BackupDAO
import fi.johannes.data.io.BackupIO
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

/**
 * Johannes on 14.1.2018.
 */
object BackupServiceFactory {
    fun createService(bfs: BackupIO, dao: BackupDAO, readyHandler: Handler<AsyncResult<BackupService>>): BackupService {
        return BackupServiceImpl(bfs, dao, readyHandler)
    }
}