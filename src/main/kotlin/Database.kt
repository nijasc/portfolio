package dev.nilsschlegel

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.net.URI
import java.net.URLDecoder
import kotlin.time.Clock
import kotlin.time.Instant

private const val MAX_POOL_SIZE = 10
private const val CONNECTION_TIMEOUT_MILLIS = 5_000L

private data class ConnectionSettings(
    val url: String,
    val user: String,
    val password: String,
)

private fun connectionSettings(databaseUrl: String): ConnectionSettings {
    if (databaseUrl.startsWith("jdbc:")) {
        return ConnectionSettings(databaseUrl, requireEnv("POSTGRES_USER"), requireEnv("POSTGRES_PASSWORD"))
    }
    val uri = URI(databaseUrl)
    val credentials =
        uri.rawUserInfo
            .orEmpty()
            .split(':', limit = 2)
            .map { URLDecoder.decode(it, Charsets.UTF_8) }
    val port = if (uri.port == -1) "" else ":${uri.port}"
    val query = uri.rawQuery?.let { "?$it" }.orEmpty()
    return ConnectionSettings(
        "jdbc:postgresql://${uri.host}$port${uri.rawPath}$query",
        credentials.getOrElse(0) { "" },
        credentials.getOrElse(1) { "" },
    )
}

object Visits : Table("visits") {
    val id = integer("id").autoIncrement()
    val at = timestamp("at")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class VisitCount(
    val visits: Long,
    val at: Instant,
)

fun Application.configureDatabase() {
    monitor.subscribe(ApplicationStarted) {
        val settings = connectionSettings(requireEnv("DATABASE_URL"))
        val dataSource =
            HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = settings.url
                    username = settings.user
                    password = settings.password
                    maximumPoolSize = MAX_POOL_SIZE
                    connectionTimeout = CONNECTION_TIMEOUT_MILLIS
                },
            )
        monitor.subscribe(ApplicationStopped) { dataSource.close() }
        Database.connect(dataSource)
        Flyway
            .configure()
            .dataSource(dataSource)
            .load()
            .migrate()
    }
}

fun Route.databaseRoutes() {
    post("/api/visits") {
        val now = Clock.System.now()
        val count =
            withContext(Dispatchers.IO) {
                transaction {
                    Visits.insert { it[at] = now }
                    Visits.selectAll().count()
                }
            }
        call.respond(HttpStatusCode.Created, VisitCount(count, now))
    }
}
