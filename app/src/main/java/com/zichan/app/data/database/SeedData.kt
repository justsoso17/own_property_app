package com.zichan.app.data.database

import com.zichan.app.data.entity.CategoryEntity
import com.zichan.app.data.entity.LocationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedData @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun seed() {
        if (database.categoryDao().getById(1) == null) {
            database.categoryDao().insertAll(DEFAULT_CATEGORIES)
        }
        if (database.locationDao().getById(1) == null) {
            database.locationDao().insertAll(DEFAULT_LOCATIONS)
        }
    }

    companion object {
        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(1, "电子产品", "devices"),
            CategoryEntity(2, "家具家居", "chair"),
            CategoryEntity(3, "收藏品", "diamond"),
            CategoryEntity(4, "图书", "book"),
            CategoryEntity(5, "服装", "apparel"),
            CategoryEntity(6, "软件", "code"),
            CategoryEntity(7, "订阅服务", "subscriptions"),
            CategoryEntity(8, "数字账号", "account_circle"),
            CategoryEntity(9, "域名", "language"),
            CategoryEntity(10, "其他", "more_horiz"),
        )

        val DEFAULT_LOCATIONS = listOf(
            LocationEntity(1, "卧室"),
            LocationEntity(2, "客厅"),
            LocationEntity(3, "书房"),
            LocationEntity(4, "办公室"),
            LocationEntity(5, "父母家"),
            LocationEntity(6, "其他"),
        )
    }
}
