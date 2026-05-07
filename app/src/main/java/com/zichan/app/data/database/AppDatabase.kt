package com.zichan.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zichan.app.data.dao.AssetDao
import com.zichan.app.data.dao.AssetLogDao
import com.zichan.app.data.dao.CategoryDao
import com.zichan.app.data.dao.LendRecordDao
import com.zichan.app.data.dao.LocationDao
import com.zichan.app.data.dao.PersonDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import com.zichan.app.data.entity.CategoryEntity
import com.zichan.app.data.entity.LendRecordEntity
import com.zichan.app.data.entity.LocationEntity
import com.zichan.app.data.entity.PersonEntity

@Database(
    entities = [
        CategoryEntity::class,
        LocationEntity::class,
        AssetEntity::class,
        PersonEntity::class,
        LendRecordEntity::class,
        AssetLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun locationDao(): LocationDao
    abstract fun assetDao(): AssetDao
    abstract fun personDao(): PersonDao
    abstract fun lendRecordDao(): LendRecordDao
    abstract fun assetLogDao(): AssetLogDao
}
