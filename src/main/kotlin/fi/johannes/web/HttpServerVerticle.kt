package fi.johannes.web

import com.github.salomonbrys.kodein.*
import fi.johannes.web.handlers.RepositoryControllers
import fi.johannes.web.handlers.repository.RepositoryController
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import io.vertx.core.logging.Logger
import io.vertx.ext.web.Router

/**
 * Johannes on 14.1.2018.
 */
class HttpServerVerticle:AbstractVerticle() {

    companion object {
        val CONFIG_HTTP_SERVER_PORT = "web.server.port"
        val CONFIG_BACKUP_QUEUE = "backup.queue"
    }

    private val logger: Logger by lazy {
        io.vertx.core.logging.LoggerFactory.getLogger(this::class.java)
    }

    private val backupQueueAddress by lazy {
        config().getString(CONFIG_BACKUP_QUEUE, "wikidb.queue")
    }

    private val backupEventBus by lazy {
        vertx.eventBus()
    }

    val httpModule by lazy {
        Kodein {
            bind<EventBus>("backupEventBus") with singleton { backupEventBus }
            constant("backupQueueAddress") with backupQueueAddress
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

    private fun setupRouter(router: Router): Router {

        val controllers = RepositoryControllers(backupQueueAddress, backupEventBus)

        // todo could use string tags to even less coupling
        val repositoryController = controllers.injector.instance<RepositoryController>()
        router.get("/:id/").handler(repositoryController::get);
        router.get("/save").handler(repositoryController::save);

        return router
    }

}