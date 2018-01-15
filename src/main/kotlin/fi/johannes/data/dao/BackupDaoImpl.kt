package fi.johannes.data.dao

import io.vertx.core.AsyncResult
import io.vertx.core.json.JsonArray
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.sql.UpdateResult

/**
 * Johannes on 14.1.2018.
 */
class BackupDaoImpl(val sqlClient: SQLClient) : BackupDao {

    private val logger = LoggerFactory.getLogger(BackupDaoImpl::class.java)

    private val createTable = """
        CREATE TABLE IF NOT EXISTS Backups (Id integer identity primary key,
        Title varchar(255),
        Filename varchar(255) unique,
        CreatedAt date)
        """

    private val getLatest = """
        SELECT Id, Title, Filename
        FROM Backups
        INNER JOIN(
            SELECT  Title as tt,
                    MAX(CreatedAt) as mx
            FROM Backups
            WHERE Backups.Title = ?
            GROUP BY Title
            ) maxDates
        ON Backups.Title = maxDates.tt
            AND Backups.CreatedAt = maxDates.mx
        """

    private val save = """
        insert into Backups values (NULL, ?, ?, ?)
        """

    private fun fetchHandler(res: AsyncResult<ResultSet>,
                             success: (ResultSet) -> Unit,
                             error: (Throwable) -> Unit) {
        if (res.succeeded()) {
            success(res.result())
        } else {
            error(res.cause())
        }
    }

    private fun updateHandler(res: AsyncResult<UpdateResult>,
                              success: () -> Unit,
                              error: (Throwable) -> Unit) {
        if (res.succeeded()) {
            success()
        } else {
            error(res.cause())
        }
    }

    override fun createTable(success: () -> Unit,
                             connectionError: (Throwable) -> Unit,
                             createError: (Throwable) -> Unit) {
        sqlClient.getConnection { ar ->
            if (ar.failed()) {
                connectionError(ar.cause())
            } else {
                logger.info("Established database connection successfully")
                val connection = ar.result()
                connection.execute(createTable) { create ->
                    connection.close()
                    if (create.failed()) {
                        createError(create.cause())
                    } else {
                        logger.info("Initialized backups table successfully")
                        success()
                    }
                }
            }
        }
    }

    override fun latest(params: JsonArray,
                        success: (ResultSet) -> Unit,
                        error: (Throwable) -> Unit) {
        sqlClient.queryWithParams(getLatest, params, { res ->
            fetchHandler(res, success, error)
        })
    }


    override fun save(params: JsonArray,
                      success: () -> Unit,
                      error: (Throwable) -> Unit) {
        sqlClient.updateWithParams(save, params, { res ->
            updateHandler(res, success, error)
        })
    }


}