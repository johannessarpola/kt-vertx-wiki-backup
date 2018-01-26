package fi.johannes.web

import com.github.salomonbrys.kodein.*
import fi.johannes.data.services.proxy.BackupService
import fi.johannes.web.handlers.repository.BackupControllers
import fi.johannes.web.handlers.repository.controllers.RepositoryController
import fi.johannes.web.handlers.status.StatusControllers
import fi.johannes.web.handlers.status.controllers.StatusController
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

        val portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8090)
        server
                .requestHandler(router::accept)
                .listen(portNumber, { ar ->
                    if (ar.succeeded()) {
                        logger.info("HTTP server running on port " + portNumber)
                        startFuture.complete()
                    } else {
                        logger.error("Could not start a HTTP server", ar.cause())
                        startFuture.fail(ar.cause())
                    }
                })
    }

    override fun stop(stopFuture: Future<Void>) {
        super.stop(stopFuture)
    }

    private fun getBackupService(): BackupService {
        return ServiceProxyBuilder(vertx)
                .setAddress(config().getString(CONFIG_BACKUPS_QUEUE, "db.queue"))
                .build(BackupService::class.java)
    }

    private fun backupRouter(): Router {
        val backupRouter = Router.router(vertx)
        val backupControllers = BackupControllers(getBackupService())
        val repositoryController = backupControllers.injector.instance<RepositoryController>()

        backupRouter.get("/:title/latest/").handler(repositoryController::get)
        backupRouter.post().handler(BodyHandler.create())
        backupRouter.post("/save").handler(repositoryController::save)

        return backupRouter
    }

    private fun statusRouter(): Router {
        val statusRouter = Router.router(vertx)
        val statusControllers = StatusControllers()
        val statusController = statusControllers.injector.instance<StatusController>()

        statusRouter.get("/service-status").handler(statusController::get)

        return statusRouter
    }

    private fun setupRouter(router: Router): Router {
        router.mountSubRouter("/backup", backupRouter())
        router.mountSubRouter("/monitor", statusRouter())
        return router
    }

}