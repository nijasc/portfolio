package dev.nilsschlegel.blog.application

import dev.nilsschlegel.blog.domain.Blog

interface BlogRepository {
    suspend fun save(blog: Blog)

    suspend fun all(): List<Blog>
}
