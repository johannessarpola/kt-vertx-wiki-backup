package fi.johannes.data.dao

import io.vertx.core.json.JsonArray
import io.vertx.ext.sql.ResultSet

/**
 * Johannes on 14.1.2018.
 */
interface BackupDao {
    fun createTable(success: () -> Unit, connectionError: (Throwable) -> Unit, createError: (Throwable) -> Unit)
    fun getLatest(params: JsonArray, success: (ResultSet) -> Unit, error: (Throwable) -> Unit)
}