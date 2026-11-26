package dev.nilsschlegel.blog.domain

import dev.nilsschlegel.kernel.DomainException

@JvmInline
value class BlogId(
    val value: String,
)

data class Blog(
    val id: BlogId,
    val name: String,
) {
    init {
        if (name.isBlank()) throw DomainException("Blog needs a name.")
    }
}
