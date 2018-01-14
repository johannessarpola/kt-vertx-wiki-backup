package fi.johannes.web

import com.github.salomonbrys.kodein.*
import fi.johannes.data.services.proxy.BackupService
import fi.johannes.web.handlers.RepositoryControllers
import fi.johannes.web.handlers.repository.RepositoryController
import fi.johannes.web.handlers.status.StatusController
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.logging.Logger
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.serviceproxy.ServiceProxyBuilder

/**
 * Johannes on 14.1.2018.
 */
class HttpServerVerticle : AbstractVerticle() {

    companion object {
        val CONFIG_HTTP_SERVER_PORT = "web.server.port"
        val CONFIG_BACKUPS_QUEUE = "backup.queue"
    }

    private val logger: Logger by lazy {
        io.vertx.core.logging.LoggerFactory.getLogger(this::class.java)
    }

    private val backupQueueAddress by lazy {
        config().getString(CONFIG_BACKUPS_QUEUE, "wikidb.queue")
    }

    private val backupEventBus by lazy {
        vertx.eventBus()
    }

    val modules by lazy {
        Kodein {

        }
    }

    override fun start(startFuture: Future<Void>) {

        val server = vertx.createHttpServer()

        val router = Router.router(vertx)
        setupRouter(router)

        val portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8090);
        server
                .requestHandler(router::accept)
                .listen(portNumber, { ar ->
                    if (ar.succeeded()) {
                        logger.info("HTTP server running on port " + portNumber);
                        startFuture.complete();
                    } else {
                        logger.error("Could not start a HTTP server", ar.cause());
                        startFuture.fail(ar.cause());
                    }
                });
    }

    override fun stop(stopFuture: Future<Void>) {
        super.stop(stopFuture)
    }

    private fun getBackupService(): BackupService {
        return ServiceProxyBuilder(vertx)
                .setAddress(config().getString(CONFIG_BACKUPS_QUEUE, "db.queue"))
                .build(BackupService::class.java)
    }

    private fun setupRouter(router: Router): Router {

        val controllers = RepositoryControllers(getBackupService())

        val repositoryController = controllers.injector.instance<RepositoryController>()
        val statusController = controllers.injector.instance<StatusController>()

        router.get("/backups/latest/:title").handler(repositoryController::get)
        router.get("/backups/service-status").handler(statusController::get)
        router.post().handler(BodyHandler.create())
        router.post("/backups/save").handler(repositoryController::save)

        return router
    }

}