package dev.nilsschlegel.blog.application

import dev.nilsschlegel.blog.domain.Blog
import dev.nilsschlegel.blog.domain.BlogId
import java.util.UUID

class BlogService(
    private val repository: BlogRepository,
) {
    suspend fun create(name: String): Blog {
        val blog = Blog(BlogId(UUID.randomUUID().toString()), name.trim())
        repository.save(blog)
        return blog
    }

    suspend fun list(): List<Blog> = repository.all()
}
