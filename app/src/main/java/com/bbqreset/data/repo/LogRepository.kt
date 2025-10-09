package com.bbqreset.data.repo

import com.bbqreset.data.db.dao.LogDao
import com.bbqreset.data.db.entity.LogEntity
import java.time.Clock
import java.time.Instant

class LogRepository(
    private val logDao: LogDao,
    private val clock: Clock
) {
    suspend fun record(
        actor: String,
        action: String,
        meta: Map<String, Any?>
    ) {
        val now = Instant.now(clock).epochSecond
        val metaJson = meta.entries
            .joinToString(prefix = "{", postfix = "}") { (key, value) ->
                val serializedValue = when (value) {
                    null -> "null"
                    is Number, is Boolean -> value.toString()
                    else -> "\"${value.toString().escapeJson()}\""
                }
                "\"${key.escapeJson()}\":$serializedValue"
            }

        logDao.insert(
            LogEntity(
                ts = now,
                actor = actor,
                action = action,
                metaJson = metaJson
            )
        )
    }

    private fun String.escapeJson(): String {
        return this
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
    }
}
