package dev.nilsschlegel.blog.adapters.web

import dev.nilsschlegel.blog.application.BlogService
import dev.nilsschlegel.blog.domain.Blog
import io.github.nijasc.ktor.svelte.Validated
import io.github.nijasc.ktor.svelte.Validator
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.JsonSchema
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

private const val MAX_NAME_LENGTH = 200

@Serializable
data class NewBlog(
    @JsonSchema.MaxLength(MAX_NAME_LENGTH)
    val name: String,
) : Validated {
    override fun Validator.rules() {
        check(::name, name.isNotBlank()) { "Enter a name." }
    }
}

@Serializable
data class BlogView(
    val id: String,
    val name: String,
)

fun Route.blogRoutes(service: BlogService) {
    route("/api/blog") {
        get {
            call.respond(service.list().map { it.toView() })
        }
        post {
            val request = call.receive<NewBlog>()
            call.respond(HttpStatusCode.Created, service.create(request.name).toView())
        }
    }
}

private fun Blog.toView() = BlogView(id.value, name)
