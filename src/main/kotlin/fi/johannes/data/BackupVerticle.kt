package fi.johannes.data

import com.github.salomonbrys.kodein.*
import fi.johannes.data.dao.BackupDao
import fi.johannes.data.dao.BackupDaoImpl
import fi.johannes.data.services.proxy.BackupService
import fi.johannes.data.services.proxy.BackupServiceFactory
import fi.johannes.data.services.proxy.BackupServiceImpl
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLClient
import io.vertx.serviceproxy.ServiceBinder

/**
 * Johannes on 14.1.2018.
 */
class BackupVerticle:AbstractVerticle() {

    companion object {
        val CONFIG_BACKUPS_FILE_STORE = "backup.file-store"
        val CONFIG_BACKUPS_FILE_STORE_FORMAT = "backup.file-format"
        val CONFIG_BACKUPS_QUEUE = "backup.queue"
        val CONFIG_BACKUPS_JDBC_MAX_POOL_SIZE = "wikidb.jdbc.max_pool_size";
        val CONFIG_BACKUPS_JDBC_URL = "wikidb.jdbc.url"
        val CONFIG_BACKUPS_JDBC_DRIVER_CLASS = "wikidb.jdbc.driver_class"
        // queries file?
    }

    private val logger: Logger  by lazy {
        io.vertx.core.logging.LoggerFactory.getLogger(this::class.java)
    }

    private val dbClient: JDBCClient by lazy {
        JDBCClient.createShared(vertx, JsonObject()
                .put("url", config().getString(CONFIG_BACKUPS_JDBC_URL, "jdbc:hsqldb:file:db/backups"))
                .put("driver_class", config().getString(CONFIG_BACKUPS_JDBC_DRIVER_CLASS, "org.hsqldb.jdbcDriver"))
                .put("max_pool_size", config().getInteger(CONFIG_BACKUPS_JDBC_MAX_POOL_SIZE, 30)))
    }

    private val modules by lazy {
        Kodein {
            bind<SQLClient>() with singleton { dbClient }
            bind<BackupDao>() with singleton {
                BackupDaoImpl(dbClient)
            }
        }
    }

    override fun start(startFuture: Future<Void>) {
        logger.info("Starting BackupVerticle")
        val now = System.currentTimeMillis()
        val dao:BackupDao =  modules.instance()
        BackupServiceFactory.createService(dao, Handler { ar ->
            if(ar.succeeded()) {
                ServiceBinder(vertx)
                        .setAddress(config().getString(CONFIG_BACKUPS_QUEUE, "db.queue"))
                        .register(BackupService::class.java, ar.result())
                startFuture.complete()
            }
            else {
                startFuture.fail(ar.cause())
            }
        })

    }


    override fun stop(stopFuture: Future<Void>) {
        dbClient.close()
    }

    // todo save to disk
}