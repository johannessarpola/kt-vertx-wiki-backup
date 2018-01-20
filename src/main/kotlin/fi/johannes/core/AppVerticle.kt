package fi.johannes.core

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import fi.johannes.data.BackupVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory

class AppVerticle : AbstractVerticle() {

    private val logger: Logger  by lazy {
        LoggerFactory.getLogger("AppVerticle")
    }

    val appModule = Kodein {
        bind<Logger>() with singleton { logger }
    }

    override fun start(startFuture: Future<Void>) {

        val backupDeployment = Future.future<String>()
        vertx.deployVerticle(BackupVerticle(), backupDeployment.completer());

        backupDeployment.compose { id ->
            val httpDeployment: Future<String> = Future.future<String>()
            vertx.deployVerticle(
                    "fi.johannes.web.HttpServerVerticle",
                    DeploymentOptions().setInstances(2),
                    httpDeployment.completer())
            httpDeployment
        }.setHandler { ar ->
            if (ar.succeeded()) {
                startFuture.complete()
            } else {
                startFuture.fail(ar.cause())
            }
        };
    }

    override fun stop(stopFuture: Future<Void>?) {
        vertx.deploymentIDs().forEach( vertx::undeploy);
    }
}


