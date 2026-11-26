package dev.nilsschlegel.blog.adapters.persistence

import dev.nilsschlegel.blog.application.BlogRepository
import dev.nilsschlegel.blog.domain.Blog
import dev.nilsschlegel.blog.domain.BlogId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert

private const val ID_LENGTH = 36
private const val NAME_LENGTH = 200

object BlogTable : Table("blog_blog") {
    val id = varchar("id", ID_LENGTH)
    val name = varchar("name", NAME_LENGTH)

    override val primaryKey = PrimaryKey(id)
}

class ExposedBlogRepository : BlogRepository {
    override suspend fun save(blog: Blog) {
        withContext(Dispatchers.IO) {
            transaction {
                BlogTable.upsert {
                    it[BlogTable.id] = blog.id.value
                    it[BlogTable.name] = blog.name
                }
            }
        }
    }

    override suspend fun all(): List<Blog> =
        withContext(Dispatchers.IO) {
            transaction {
                BlogTable
                    .selectAll()
                    .orderBy(BlogTable.name)
                    .map { Blog(BlogId(it[BlogTable.id]), it[BlogTable.name]) }
            }
        }
}
