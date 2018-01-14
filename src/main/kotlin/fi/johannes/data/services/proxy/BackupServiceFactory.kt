package fi.johannes.data.services.proxy

import fi.johannes.data.dao.BackupDao
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx

/**
 * Johannes on 14.1.2018.
 */
object BackupServiceFactory {
    fun createService(dao: BackupDao, readyHandler: Handler<AsyncResult<BackupService>>): BackupService {
        return BackupServiceImpl(dao, readyHandler)
    }
}