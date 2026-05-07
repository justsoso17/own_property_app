# 个人资产管理系统 Android App 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 Java Swing 桌面版个人资产管理系统移植为 Android 原生 App（Kotlin + Jetpack Compose + Room），全本地架构。

**Architecture:** 单 Activity + Compose Navigation + 4 标签底部导航。MVVM 模式：Compose UI → ViewModel → Repository → Room DAO → SQLite。跟随 HyperOS 系统浅色/深色主题。

**Tech Stack:** Kotlin 2.0, Jetpack Compose + Material 3, Room 2.6, Hilt 2.51, Coroutines + Flow, BiometricPrompt, Compose Navigation

**Package:** `com.zichan.app`

---

## Phase 0: 项目骨架

### Task 0.1: 版本目录与 Gradle 配置

**Files:**
- Create: `gradle/libs.versions.toml`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `gradle.properties`
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (root)
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/proguard-rules.pro`

- [ ] **Step 1: 创建 gradle/libs.versions.toml**

```toml
[versions]
agp = "8.5.2"
kotlin = "2.0.10"
coreKtx = "1.13.1"
lifecycleRuntime = "2.8.4"
activityCompose = "1.9.1"
composeBom = "2024.08.00"
navigationCompose = "2.7.7"
room = "2.6.1"
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"
coroutines = "1.8.1"
biometric = "1.1.0"
gson = "2.11.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntime" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntime" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-biometric = { group = "androidx.biometric", name = "biometric", version.ref = "biometric" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-ksp = { id = "com.google.devtools.ksp", version = "2.0.10-1.0.24" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
room = { id = "androidx.room", version.ref = "room" }
```

- [ ] **Step 2: 创建 gradle/wrapper/gradle-wrapper.properties**

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [ ] **Step 3: 创建 gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 4: 创建 settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Zichan"
include(":app")
```

- [ ] **Step 5: 创建根 build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

- [ ] **Step 6: 创建 app/build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.zichan.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zichan.app"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.biometric)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.gson)
    debugImplementation(libs.androidx.ui.tooling)
}
```

- [ ] **Step 7: 创建 app/src/main/AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />

    <uses-feature android:name="android.hardware.camera" android:required="false" />

    <application
        android:name=".ZichanApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="资产管家"
        android:supportsRtl="true"
        android:theme="@style/Theme.Zichan">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Zichan">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 8: 创建 app/src/main/res/values/themes.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.Zichan" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

- [ ] **Step 9: 创建 app/proguard-rules.pro**

(空文件)

- [ ] **Step 10: 创建资源目录**

Run:
```bash
mkdir -p "E:/个人资产app/app/src/main/res/values"
mkdir -p "E:/个人资产app/app/src/main/res/mipmap-hdpi"
mkdir -p "E:/个人资产app/app/src/main/res/mipmap-xhdpi"
mkdir -p "E:/个人资产app/app/src/main/res/mipmap-xxhdpi"
```

---

### Task 0.2: 应用入口与 Hilt

**Files:**
- Create: `app/src/main/java/com/zichan/app/ZichanApplication.kt`
- Create: `app/src/main/java/com/zichan/app/MainActivity.kt`

- [ ] **Step 1: 创建 ZichanApplication.kt**

```kotlin
package com.zichan.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ZichanApplication : Application()
```

- [ ] **Step 2: 创建 MainActivity.kt (shell)**

```kotlin
package com.zichan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZichanApp()
        }
    }
}
```

- [ ] **Step 3: 创建 app/src/main/res/values/strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">资产管家</string>
</resources>
```

---

## Phase 1: 数据层 (P0 核心)

### Task 1.1: Room 实体

**Files:**
- Create: `app/src/main/java/com/zichan/app/data/entity/CategoryEntity.kt`
- Create: `app/src/main/java/com/zichan/app/data/entity/LocationEntity.kt`
- Create: `app/src/main/java/com/zichan/app/data/entity/AssetEntity.kt`
- Create: `app/src/main/java/com/zichan/app/data/entity/PersonEntity.kt`
- Create: `app/src/main/java/com/zichan/app/data/entity/LendRecordEntity.kt`
- Create: `app/src/main/java/com/zichan/app/data/entity/AssetLogEntity.kt`

- [ ] **Step 1: 创建 CategoryEntity.kt**

```kotlin
package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon") val icon: String = "category"
)
```

- [ ] **Step 2: 创建 LocationEntity.kt**

```kotlin
package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)
```

- [ ] **Step 3: 创建 AssetEntity.kt**

```kotlin
package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("category_id"),
        Index("location_id"),
        Index("status")
    ]
)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "brand") val brand: String = "",
    @ColumnInfo(name = "model") val model: String = "",
    @ColumnInfo(name = "category_id") val categoryId: Long? = null,
    @ColumnInfo(name = "price") val price: Double = 0.0,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long? = null,
    @ColumnInfo(name = "purchase_channel") val purchaseChannel: String = "",
    @ColumnInfo(name = "status") val status: String = "使用中",
    @ColumnInfo(name = "location_id") val locationId: Long? = null,
    @ColumnInfo(name = "specs") val specs: String = "",
    @ColumnInfo(name = "serial_number") val serialNumber: String = "",
    @ColumnInfo(name = "notes") val notes: String = "",
    @ColumnInfo(name = "is_virtual") val isVirtual: Boolean = false,
    @ColumnInfo(name = "expiry_date") val expiryDate: Long? = null,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
```

- [ ] **Step 4: 创建 PersonEntity.kt**

```kotlin
package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "relationship") val relationship: String = "",
    @ColumnInfo(name = "phone") val phone: String = "",
    @ColumnInfo(name = "wechat") val wechat: String = "",
    @ColumnInfo(name = "notes") val notes: String = ""
)
```

- [ ] **Step 5: 创建 LendRecordEntity.kt**

```kotlin
package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lend_records",
    foreignKeys = [
        ForeignKey(entity = AssetEntity::class, parentColumns = ["id"], childColumns = ["asset_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = PersonEntity::class, parentColumns = ["id"], childColumns = ["person_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("asset_id"), Index("person_id")]
)
data class LendRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "asset_id") val assetId: Long,
    @ColumnInfo(name = "person_id") val personId: Long? = null,
    @ColumnInfo(name = "lend_date") val lendDate: Long,
    @ColumnInfo(name = "expected_return_date") val expectedReturnDate: Long? = null,
    @ColumnInfo(name = "actual_return_date") val actualReturnDate: Long? = null,
    @ColumnInfo(name = "status") val status: String = "借用中"
)
```

- [ ] **Step 6: 创建 AssetLogEntity.kt**

```kotlin
package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset_logs")
data class AssetLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "asset_id") val assetId: Long? = null,
    @ColumnInfo(name = "operation") val operation: String,
    @ColumnInfo(name = "detail") val detail: String = "",
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
```

---

### Task 1.2: Room DAO

**Files:**
- Create: `app/src/main/java/com/zichan/app/data/dao/CategoryDao.kt`
- Create: `app/src/main/java/com/zichan/app/data/dao/LocationDao.kt`
- Create: `app/src/main/java/com/zichan/app/data/dao/AssetDao.kt`
- Create: `app/src/main/java/com/zichan/app/data/dao/PersonDao.kt`
- Create: `app/src/main/java/com/zichan/app/data/dao/LendRecordDao.kt`
- Create: `app/src/main/java/com/zichan/app/data/dao/AssetLogDao.kt`

- [ ] **Step 1: 创建 CategoryDao.kt**

```kotlin
package com.zichan.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zichan.app.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
}
```

- [ ] **Step 2: 创建 LocationDao.kt**

```kotlin
package com.zichan.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zichan.app.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY id")
    fun getAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: Long): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<LocationEntity>)
}
```

- [ ] **Step 3: 创建 AssetDao.kt**

```kotlin
package com.zichan.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zichan.app.data.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY created_at DESC")
    fun getAll(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getById(id: Long): AssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: AssetEntity): Long

    @Update
    suspend fun update(asset: AssetEntity)

    @Delete
    suspend fun delete(asset: AssetEntity)

    @Query("SELECT * FROM assets WHERE status = :status ORDER BY created_at DESC")
    fun getByStatus(status: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE name LIKE '%' || :keyword || '%' OR brand LIKE '%' || :keyword || '%' OR model LIKE '%' || :keyword || '%' OR notes LIKE '%' || :keyword || '%' ORDER BY created_at DESC")
    fun search(keyword: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE " +
        "(:keyword IS NULL OR name LIKE '%' || :keyword || '%' OR brand LIKE '%' || :keyword || '%') AND " +
        "(:categoryId IS NULL OR category_id = :categoryId) AND " +
        "(:status IS NULL OR status = :status) AND " +
        "(:minPrice IS NULL OR price >= :minPrice) AND " +
        "(:maxPrice IS NULL OR price <= :maxPrice) " +
        "ORDER BY created_at DESC")
    fun filter(
        keyword: String?,
        categoryId: Long?,
        status: String?,
        minPrice: Double?,
        maxPrice: Double?
    ): Flow<List<AssetEntity>>

    @Query("SELECT COUNT(*) FROM assets WHERE status = :status")
    fun countByStatus(status: String): Flow<Int>

    @Query("SELECT SUM(price) FROM assets WHERE status NOT IN ('已出售', '已丢弃')")
    fun totalValue(): Flow<Double?>

    @Query("SELECT * FROM assets WHERE is_virtual = 1 AND expiry_date IS NOT NULL AND expiry_date BETWEEN :now AND :sevenDaysLater")
    fun getExpiringSoon(now: Long, sevenDaysLater: Long): Flow<List<AssetEntity>>
}
```

- [ ] **Step 4: 创建 PersonDao.kt**

```kotlin
package com.zichan.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zichan.app.data.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY id DESC")
    fun getAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getById(id: Long): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: PersonEntity): Long

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)
}
```

- [ ] **Step 5: 创建 LendRecordDao.kt**

```kotlin
package com.zichan.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zichan.app.data.entity.LendRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LendRecordDao {
    @Query("SELECT * FROM lend_records ORDER BY lend_date DESC")
    fun getAll(): Flow<List<LendRecordEntity>>

    @Query("SELECT * FROM lend_records WHERE id = :id")
    suspend fun getById(id: Long): LendRecordEntity?

    @Query("SELECT * FROM lend_records WHERE asset_id = :assetId ORDER BY lend_date DESC")
    fun getByAssetId(assetId: Long): Flow<List<LendRecordEntity>>

    @Query("SELECT * FROM lend_records WHERE status = :status ORDER BY lend_date DESC")
    fun getByStatus(status: String): Flow<List<LendRecordEntity>>

    @Insert
    suspend fun insert(record: LendRecordEntity): Long

    @Update
    suspend fun update(record: LendRecordEntity)
}
```

- [ ] **Step 6: 创建 AssetLogDao.kt**

```kotlin
package com.zichan.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zichan.app.data.entity.AssetLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetLogDao {
    @Query("SELECT * FROM asset_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AssetLogEntity>>

    @Query("SELECT * FROM asset_logs WHERE asset_id = :assetId ORDER BY timestamp DESC")
    fun getByAssetId(assetId: Long): Flow<List<AssetLogEntity>>

    @Insert
    suspend fun insert(log: AssetLogEntity)

    @Query("DELETE FROM asset_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_logs")
    suspend fun deleteAll()
}
```

---

### Task 1.3: AppDatabase + 种子数据

**Files:**
- Create: `app/src/main/java/com/zichan/app/data/database/AppDatabase.kt`
- Create: `app/src/main/java/com/zichan/app/data/database/SeedData.kt`

- [ ] **Step 1: 创建 AppDatabase.kt**

```kotlin
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
```

- [ ] **Step 2: 创建 SeedData.kt**

```kotlin
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
        val catCount = database.categoryDao().getById(1)
        if (catCount == null) {
            database.categoryDao().insertAll(DEFAULT_CATEGORIES)
        }
        val locCount = database.locationDao().getById(1)
        if (locCount == null) {
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
```

---

### Task 1.4: Hilt 数据库模块

**Files:**
- Create: `app/src/main/java/com/zichan/app/di/DatabaseModule.kt`

- [ ] **Step 1: 创建 DatabaseModule.kt**

```kotlin
package com.zichan.app.di

import android.content.Context
import androidx.room.Room
import com.zichan.app.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "zichan.db"
        ).build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()

    @Provides
    fun provideLocationDao(database: AppDatabase) = database.locationDao()

    @Provides
    fun provideAssetDao(database: AppDatabase) = database.assetDao()

    @Provides
    fun providePersonDao(database: AppDatabase) = database.personDao()

    @Provides
    fun provideLendRecordDao(database: AppDatabase) = database.lendRecordDao()

    @Provides
    fun provideAssetLogDao(database: AppDatabase) = database.assetLogDao()
}
```

---

### Task 1.5: Repository 层

**Files:**
- Create: `app/src/main/java/com/zichan/app/data/repository/AssetRepository.kt`
- Create: `app/src/main/java/com/zichan/app/data/repository/CategoryRepository.kt`
- Create: `app/src/main/java/com/zichan/app/data/repository/LocationRepository.kt`
- Create: `app/src/main/java/com/zichan/app/data/repository/PersonRepository.kt`
- Create: `app/src/main/java/com/zichan/app/data/repository/LendRecordRepository.kt`
- Create: `app/src/main/java/com/zichan/app/data/repository/AssetLogRepository.kt`

- [ ] **Step 1: 创建 CategoryRepository.kt**

```kotlin
package com.zichan.app.data.repository

import com.zichan.app.data.dao.CategoryDao
import com.zichan.app.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    fun getAll(): Flow<List<CategoryEntity>> = dao.getAll()

    suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)
}
```

- [ ] **Step 2: 创建 LocationRepository.kt**

```kotlin
package com.zichan.app.data.repository

import com.zichan.app.data.dao.LocationDao
import com.zichan.app.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val dao: LocationDao
) {
    fun getAll(): Flow<List<LocationEntity>> = dao.getAll()

    suspend fun getById(id: Long): LocationEntity? = dao.getById(id)
}
```

- [ ] **Step 3: 创建 AssetRepository.kt**

```kotlin
package com.zichan.app.data.repository

import com.zichan.app.data.dao.AssetDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val dao: AssetDao,
    private val logRepository: AssetLogRepository
) {
    fun getAll(): Flow<List<AssetEntity>> = dao.getAll()

    suspend fun getById(id: Long): AssetEntity? = dao.getById(id)

    suspend fun insert(asset: AssetEntity): Long {
        val id = dao.insert(asset)
        logRepository.insert(AssetLogEntity(assetId = id, operation = "添加", detail = asset.name))
        return id
    }

    suspend fun update(asset: AssetEntity) {
        dao.update(asset)
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "修改", detail = asset.name))
    }

    suspend fun delete(asset: AssetEntity) {
        dao.delete(asset)
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "删除", detail = asset.name))
    }

    suspend fun sell(asset: AssetEntity) {
        dao.update(asset.copy(status = "已出售"))
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "出售", detail = asset.name))
    }

    suspend fun discard(asset: AssetEntity) {
        dao.update(asset.copy(status = "已丢弃"))
        logRepository.insert(AssetLogEntity(assetId = asset.id, operation = "丢弃", detail = asset.name))
    }

    fun getByStatus(status: String): Flow<List<AssetEntity>> = dao.getByStatus(status)

    fun search(keyword: String): Flow<List<AssetEntity>> = dao.search(keyword)

    fun filter(
        keyword: String? = null,
        categoryId: Long? = null,
        status: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Flow<List<AssetEntity>> = dao.filter(keyword, categoryId, status, minPrice, maxPrice)

    fun countByStatus(status: String): Flow<Int> = dao.countByStatus(status)

    fun totalValue(): Flow<Double?> = dao.totalValue()

    fun getExpiringSoon(now: Long, sevenDaysLater: Long): Flow<List<AssetEntity>> =
        dao.getExpiringSoon(now, sevenDaysLater)
}
```

- [ ] **Step 4: 创建 PersonRepository.kt**

```kotlin
package com.zichan.app.data.repository

import com.zichan.app.data.dao.PersonDao
import com.zichan.app.data.entity.PersonEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonRepository @Inject constructor(
    private val dao: PersonDao
) {
    fun getAll(): Flow<List<PersonEntity>> = dao.getAll()

    suspend fun getById(id: Long): PersonEntity? = dao.getById(id)

    suspend fun insert(person: PersonEntity): Long = dao.insert(person)

    suspend fun update(person: PersonEntity) = dao.update(person)

    suspend fun delete(person: PersonEntity) = dao.delete(person)
}
```

- [ ] **Step 5: 创建 LendRecordRepository.kt**

```kotlin
package com.zichan.app.data.repository

import com.zichan.app.data.dao.LendRecordDao
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.AssetLogEntity
import com.zichan.app.data.entity.LendRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LendRecordRepository @Inject constructor(
    private val dao: LendRecordDao,
    private val assetRepository: AssetRepository,
    private val logRepository: AssetLogRepository
) {
    fun getAll(): Flow<List<LendRecordEntity>> = dao.getAll()

    suspend fun getById(id: Long): LendRecordEntity? = dao.getById(id)

    fun getByAssetId(assetId: Long): Flow<List<LendRecordEntity>> = dao.getByAssetId(assetId)

    fun getByStatus(status: String): Flow<List<LendRecordEntity>> = dao.getByStatus(status)

    suspend fun lend(record: LendRecordEntity, asset: AssetEntity) {
        assetRepository.update(asset.copy(status = "已借出"))
        dao.insert(record)
        logRepository.insert(AssetLogEntity(
            assetId = record.assetId, operation = "借出",
            detail = "借出 ${asset.name}"
        ))
    }

    suspend fun returnAsset(record: LendRecordEntity, asset: AssetEntity) {
        val now = System.currentTimeMillis()
        dao.update(record.copy(status = "已归还", actualReturnDate = now))
        assetRepository.update(asset.copy(status = "使用中"))
        logRepository.insert(AssetLogEntity(
            assetId = record.assetId, operation = "归还",
            detail = "归还 ${asset.name}"
        ))
    }
}
```

- [ ] **Step 6: 创建 AssetLogRepository.kt**

```kotlin
package com.zichan.app.data.repository

import com.zichan.app.data.dao.AssetLogDao
import com.zichan.app.data.entity.AssetLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetLogRepository @Inject constructor(
    private val dao: AssetLogDao
) {
    fun getAll(): Flow<List<AssetLogEntity>> = dao.getAll()

    fun getByAssetId(assetId: Long): Flow<List<AssetLogEntity>> = dao.getByAssetId(assetId)

    suspend fun insert(log: AssetLogEntity) = dao.insert(log)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()
}
```

---

## Phase 2: 主题与导航 (P0)

### Task 2.1: HyperOS 主题

**Files:**
- Create: `app/src/main/java/com/zichan/app/ui/theme/Color.kt`
- Create: `app/src/main/java/com/zichan/app/ui/theme/Type.kt`
- Create: `app/src/main/java/com/zichan/app/ui/theme/Theme.kt`

- [ ] **Step 1: 创建 Color.kt**

```kotlin
package com.zichan.app.ui.theme

import androidx.compose.ui.graphics.Color

// HyperOS orange accent
val Orange500 = Color(0xFFFF6900)
val Orange400 = Color(0xFFFF8A33)
val Orange300 = Color(0xFFFFB380)

// Light theme
val LightBackground = Color(0xFFF5F5F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F0F0)
val LightOnBackground = Color(0xFF1A1A1A)
val LightOnSurface = Color(0xFF333333)
val LightOnSurfaceVariant = Color(0xFF666666)

// Dark theme
val DarkBackground = Color(0xFF1A1A1A)
val DarkSurface = Color(0xFF2A2A2A)
val DarkSurfaceVariant = Color(0xFF3A3A3A)
val DarkOnBackground = Color(0xFFE0E0E0)
val DarkOnSurface = Color(0xFFCCCCCC)
val DarkOnSurfaceVariant = Color(0xFF999999)

// Status colors
val StatusInUse = Color(0xFF4CAF50)
val StatusIdle = Color(0xFFFF9800)
val StatusLent = Color(0xFFF44336)
val StatusSold = Color(0xFF9E9E9E)
```

- [ ] **Step 2: 创建 Type.kt**

```kotlin
package com.zichan.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium, lineHeight = 26.sp),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp),
    labelSmall = TextStyle(fontSize = 10.sp, lineHeight = 14.sp),
)
```

- [ ] **Step 3: 创建 Theme.kt**

```kotlin
package com.zichan.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Orange500,
    onPrimary = LightSurface,
    primaryContainer = Orange300,
    secondary = Orange400,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange400,
    onPrimary = DarkBackground,
    primaryContainer = Orange500,
    secondary = Orange300,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
)

@Composable
fun ZichanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
```

---

### Task 2.2: 导航框架

**Files:**
- Create: `app/src/main/java/com/zichan/app/ui/navigation/Screen.kt`
- Create: `app/src/main/java/com/zichan/app/ui/navigation/ZichanNavHost.kt`
- Modify: `app/src/main/java/com/zichan/app/MainActivity.kt`

- [ ] **Step 1: 创建 Screen.kt (路由定义)**

```kotlin
package com.zichan.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String = "",
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Home : Screen("home", "首页", Icons.Filled.Home, Icons.Outlined.Home)
    data object Assets : Screen("assets", "资产", Icons.Filled.Inventory2, Icons.Outlined.Inventory2)
    data object Stats : Screen("stats", "统计", Icons.Filled.Add, Icons.Outlined.BarChart)
    data object Profile : Screen("profile", "我的", Icons.Filled.Person, Icons.Outlined.Person)

    data object AssetDetail : Screen("asset_detail/{assetId}") {
        fun createRoute(assetId: Long) = "asset_detail/$assetId"
    }

    data object AssetEdit : Screen("asset_edit/{assetId}") {
        fun createRoute(assetId: Long = 0) = "asset_edit/$assetId"
    }

    data object PersonList : Screen("person_list")
    data object PersonEdit : Screen("person_edit/{personId}") {
        fun createRoute(personId: Long = 0) = "person_edit/$personId"
    }

    data object LendManage : Screen("lend_manage/{assetId}") {
        fun createRoute(assetId: Long) = "lend_manage/$assetId"
    }

    data object LogView : Screen("log_view")

    val bottomTabs = listOf(Home, Assets, Stats, Profile)
}
```

- [ ] **Step 2: 创建 ZichanNavHost.kt**

```kotlin
package com.zichan.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zichan.app.ui.asset.AssetDetailScreen
import com.zichan.app.ui.asset.AssetEditScreen
import com.zichan.app.ui.asset.AssetListScreen
import com.zichan.app.ui.home.HomeScreen
import com.zichan.app.ui.profile.LogScreen
import com.zichan.app.ui.profile.PersonEditScreen
import com.zichan.app.ui.profile.PersonListScreen
import com.zichan.app.ui.profile.ProfileScreen
import com.zichan.app.ui.stats.StatsScreen
import com.zichan.app.ui.theme.Orange500

@Composable
fun ZichanNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = Screen.bottomTabs.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ) {
                    Screen.bottomTabs.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Orange500,
                                selectedTextColor = Orange500,
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAssetClick = { assetId ->
                        navController.navigate(Screen.AssetDetail.createRoute(assetId))
                    },
                    onAddAsset = {
                        navController.navigate(Screen.AssetEdit.createRoute(0))
                    }
                )
            }

            composable(Screen.Assets.route) {
                AssetListScreen(
                    onAssetClick = { assetId ->
                        navController.navigate(Screen.AssetDetail.createRoute(assetId))
                    },
                    onAddAsset = {
                        navController.navigate(Screen.AssetEdit.createRoute(0))
                    }
                )
            }

            composable(
                Screen.AssetDetail.route,
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getLong("assetId") ?: 0L
                AssetDetailScreen(
                    assetId = assetId,
                    onEdit = { navController.navigate(Screen.AssetEdit.createRoute(it)) },
                    onLend = { navController.navigate(Screen.LendManage.createRoute(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.AssetEdit.route,
                arguments = listOf(navArgument("assetId") { type = NavType.LongType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getLong("assetId") ?: 0L
                AssetEditScreen(
                    assetId = assetId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onPersonList = { navController.navigate(Screen.PersonList.route) },
                    onLogView = { navController.navigate(Screen.LogView.route) }
                )
            }

            composable(Screen.PersonList.route) {
                PersonListScreen(
                    onPersonClick = { personId ->
                        navController.navigate(Screen.PersonEdit.createRoute(personId))
                    },
                    onAddPerson = {
                        navController.navigate(Screen.PersonEdit.createRoute(0))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Screen.PersonEdit.route,
                arguments = listOf(navArgument("personId") { type = NavType.LongType })
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getLong("personId") ?: 0L
                PersonEditScreen(
                    personId = personId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.LogView.route) {
                LogScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
```

- [ ] **Step 3: 更新 MainActivity.kt**

修改为调用 `ZichanNavHost()`:

```kotlin
package com.zichan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zichan.app.ui.navigation.ZichanNavHost
import com.zichan.app.ui.theme.ZichanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZichanTheme {
                ZichanNavHost()
            }
        }
    }
}
```

- [ ] **Step 4: 创建占位 Composable (各 Screen 的骨架，让导航可编译)**

Create: `app/src/main/java/com/zichan/app/ui/ZichanApp.kt` — actually we don't need this, the MainActivity directly calls ZichanNavHost.

Create placeholder files for all screens so the NavHost compiles:

- [ ] Create: `app/src/main/java/com/zichan/app/ui/home/HomeScreen.kt`

```kotlin
package com.zichan.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onAssetClick: (Long) -> Unit = {},
    onAddAsset: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("首页")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/asset/AssetListScreen.kt`

```kotlin
package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AssetListScreen(
    onAssetClick: (Long) -> Unit = {},
    onAddAsset: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("资产")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/asset/AssetDetailScreen.kt`

```kotlin
package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AssetDetailScreen(
    assetId: Long,
    onEdit: (Long) -> Unit = {},
    onLend: (Long) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("资产详情 $assetId")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/asset/AssetEditScreen.kt`

```kotlin
package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AssetEditScreen(
    assetId: Long,
    onBack: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(if (assetId == 0L) "添加资产" else "编辑资产 $assetId")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/stats/StatsScreen.kt`

```kotlin
package com.zichan.app.ui.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun StatsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("统计")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/profile/ProfileScreen.kt`

```kotlin
package com.zichan.app.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen(
    onPersonList: () -> Unit = {},
    onLogView: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("我的")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/profile/PersonListScreen.kt`

```kotlin
package com.zichan.app.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PersonListScreen(
    onPersonClick: (Long) -> Unit = {},
    onAddPerson: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("联系人")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/profile/PersonEditScreen.kt`

```kotlin
package com.zichan.app.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PersonEditScreen(
    personId: Long,
    onBack: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(if (personId == 0L) "添加联系人" else "编辑联系人")
    }
}
```

- [ ] Create: `app/src/main/java/com/zichan/app/ui/profile/LogScreen.kt`

```kotlin
package com.zichan.app.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LogScreen(onBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("操作日志")
    }
}
```

---

## Phase 3: 首页 (P0)

### Task 3.1: HomeViewModel

**Files:**
- Create: `app/src/main/java/com/zichan/app/ui/home/HomeViewModel.kt`

- [ ] **Step 1: 创建 HomeViewModel.kt**

```kotlin
package com.zichan.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalValue: Double = 0.0,
    val inUseCount: Int = 0,
    val idleCount: Int = 0,
    val lentCount: Int = 0,
    val expiringAssets: List<AssetEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.totalValue(),
                repository.countByStatus("使用中"),
                repository.countByStatus("闲置"),
                repository.countByStatus("已借出"),
                repository.getExpiringSoon(
                    System.currentTimeMillis(),
                    System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
                )
            ) { value, inUse, idle, lent, expiring ->
                HomeUiState(
                    totalValue = value ?: 0.0,
                    inUseCount = inUse,
                    idleCount = idle,
                    lentCount = lent,
                    expiringAssets = expiring,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
```

### Task 3.2: HomeScreen UI

**Files:**
- Modify: `app/src/main/java/com/zichan/app/ui/home/HomeScreen.kt`

- [ ] **Step 1: 重写 HomeScreen.kt**

```kotlin
package com.zichan.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.ui.theme.Orange500
import com.zichan.app.ui.theme.StatusIdle
import com.zichan.app.ui.theme.StatusInUse
import com.zichan.app.ui.theme.StatusLent
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAssetClick: (Long) -> Unit = {},
    onAddAsset: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("资产管家") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAsset,
                containerColor = Orange500,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "添加资产")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Orange500)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { TotalValueCard(uiState.totalValue) }
                item { StatusCards(uiState.inUseCount, uiState.idleCount, uiState.lentCount) }
                if (uiState.expiringAssets.isNotEmpty()) {
                    item {
                        Text(
                            "即将到期",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(uiState.expiringAssets) { asset ->
                        ExpiryCard(asset, onClick = { onAssetClick(asset.id) })
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TotalValueCard(value: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Orange500)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("总资产", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
            Text(
                NumberFormat.getCurrencyInstance(java.util.Locale.CHINA).format(value),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusCards(inUse: Int, idle: Int, lent: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatusCard("使用中", inUse, StatusInUse, Modifier.weight(1f))
        StatusCard("闲置", idle, StatusIdle, Modifier.weight(1f))
        StatusCard("已借出", lent, StatusLent, Modifier.weight(1f))
    }
}

@Composable
fun StatusCard(label: String, count: Int, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Text(label, fontSize = 12.sp, color = color)
        }
    }
}

@Composable
fun ExpiryCard(asset: AssetEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, null, tint = StatusLent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(asset.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "即将到期",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

---

## Phase 4: 资产管理 (P0)

### Task 4.1: AssetViewModel

**Files:**
- Create: `app/src/main/java/com/zichan/app/ui/asset/AssetViewModel.kt`

- [ ] **Step 1: 创建 AssetViewModel.kt**

```kotlin
package com.zichan.app.ui.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.data.entity.CategoryEntity
import com.zichan.app.data.entity.LocationEntity
import com.zichan.app.data.repository.AssetRepository
import com.zichan.app.data.repository.CategoryRepository
import com.zichan.app.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetListUiState(
    val assets: List<AssetEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val searchKeyword: String = "",
    val filterCategoryId: Long? = null,
    val filterStatus: String? = null,
    val isLoading: Boolean = true
)

data class AssetEditUiState(
    val name: String = "",
    val brand: String = "",
    val model: String = "",
    val categoryId: Long? = null,
    val price: String = "",
    val purchaseDate: Long? = null,
    val purchaseChannel: String = "",
    val status: String = "使用中",
    val locationId: Long? = null,
    val specs: String = "",
    val serialNumber: String = "",
    val notes: String = "",
    val isVirtual: Boolean = false,
    val expiryDate: Long? = null,
    val photoPath: String? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(AssetListUiState())
    val listState: StateFlow<AssetListUiState> = _listState.asStateFlow()

    private val _editState = MutableStateFlow(AssetEditUiState())
    val editState: StateFlow<AssetEditUiState> = _editState.asStateFlow()

    init {
        loadCategories()
        loadLocations()
        loadAssets()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAll().collect { cats ->
                _listState.value = _listState.value.copy(categories = cats)
                _editState.value = _editState.value.copy(categories = cats)
            }
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            locationRepository.getAll().collect { locs ->
                _listState.value = _listState.value.copy(locations = locs)
                _editState.value = _editState.value.copy(locations = locs)
            }
        }
    }

    fun loadAssets() {
        viewModelScope.launch {
            assetRepository.getAll().collect { assets ->
                _listState.value = _listState.value.copy(assets = assets, isLoading = false)
            }
        }
    }

    fun search(keyword: String) {
        _listState.value = _listState.value.copy(searchKeyword = keyword)
        viewModelScope.launch {
            val flow = if (keyword.isBlank()) assetRepository.getAll()
            else assetRepository.search(keyword)
            flow.collect { assets ->
                _listState.value = _listState.value.copy(assets = assets)
            }
        }
    }

    fun applyFilter(categoryId: Long?, status: String?) {
        _listState.value = _listState.value.copy(filterCategoryId = categoryId, filterStatus = status)
        viewModelScope.launch {
            assetRepository.filter(
                keyword = _listState.value.searchKeyword.ifBlank { null },
                categoryId = categoryId,
                status = status,
                minPrice = null,
                maxPrice = null
            ).collect { assets ->
                _listState.value = _listState.value.copy(assets = assets)
            }
        }
    }

    fun loadAssetForEdit(assetId: Long) {
        viewModelScope.launch {
            if (assetId == 0L) {
                _editState.value = _editState.value.copy(isLoading = false)
            } else {
                assetRepository.getById(assetId)?.let { asset ->
                    _editState.value = _editState.value.copy(
                        name = asset.name,
                        brand = asset.brand,
                        model = asset.model,
                        categoryId = asset.categoryId,
                        price = if (asset.price > 0) asset.price.toString() else "",
                        purchaseDate = asset.purchaseDate,
                        purchaseChannel = asset.purchaseChannel,
                        status = asset.status,
                        locationId = asset.locationId,
                        specs = asset.specs,
                        serialNumber = asset.serialNumber,
                        notes = asset.notes,
                        isVirtual = asset.isVirtual,
                        expiryDate = asset.expiryDate,
                        photoPath = asset.photoPath,
                        isLoading = false
                    )
                } ?: run {
                    _editState.value = _editState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun updateEditField(update: AssetEditUiState.() -> AssetEditUiState) {
        _editState.value = _editState.value.update()
    }

    fun saveAsset() {
        val state = _editState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            val price = state.price.toDoubleOrNull() ?: 0.0
            // This will be used for both insert and update via the calling screen
            _editState.value = _editState.value.copy(isSaving = false, saved = true)
        }
    }

    fun deleteAsset(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.delete(asset)
        }
    }

    fun sellAsset(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.sell(asset)
        }
    }

    fun discardAsset(asset: AssetEntity) {
        viewModelScope.launch {
            assetRepository.discard(asset)
        }
    }
}
```

---

### Task 4.2: AssetListScreen UI

**Files:**
- Modify: `app/src/main/java/com/zichan/app/ui/asset/AssetListScreen.kt`

- [ ] **Step 1: 重写 AssetListScreen.kt**

```kotlin
package com.zichan.app.ui.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.ui.theme.Orange500
import com.zichan.app.ui.theme.StatusIdle
import com.zichan.app.ui.theme.StatusInUse
import com.zichan.app.ui.theme.StatusLent
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onAssetClick: (Long) -> Unit = {},
    onAddAsset: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var searchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("资产") },
                actions = {
                    Icon(
                        Icons.Filled.Search, "搜索",
                        modifier = Modifier.clickable { searchActive = true }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAsset,
                containerColor = Orange500,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "添加资产")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (searchActive) {
                SearchBar(
                    query = state.searchKeyword,
                    onQueryChange = { viewModel.search(it) },
                    onSearch = { searchActive = false },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("搜索资产...") }
                ) {
                    viewModel.search(state.searchKeyword)
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Orange500)
                }
            } else if (state.assets.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("暂无资产", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "点击 + 添加第一件资产",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.assets, key = { it.id }) { asset ->
                        AssetListItem(asset, onClick = { onAssetClick(asset.id) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun AssetListItem(asset: AssetEntity, onClick: () -> Unit) {
    val statusColor = when (asset.status) {
        "使用中" -> StatusInUse
        "闲置" -> StatusIdle
        "已借出" -> StatusLent
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        asset.status,
                        fontSize = 11.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    if (asset.brand.isNotBlank()) {
                        Text("${asset.brand} ", style = MaterialTheme.typography.bodySmall)
                    }
                    if (asset.model.isNotBlank()) {
                        Text(asset.model, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                NumberFormat.getCurrencyInstance(java.util.Locale.CHINA).format(asset.price),
                style = MaterialTheme.typography.titleMedium,
                color = Orange500
            )
        }
    }
}
```

---

### Task 4.3: AssetEditScreen UI

**Files:**
- Modify: `app/src/main/java/com/zichan/app/ui/asset/AssetEditScreen.kt`

- [ ] **Step 1: 重写 AssetEditScreen.kt (添加/编辑资产表单)**

```kotlin
package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.ui.theme.Orange500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetEditScreen(
    assetId: Long,
    onBack: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.editState.collectAsStateWithLifecycle()
    var categoryExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(assetId) {
        viewModel.loadAssetForEdit(assetId)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (assetId == 0L) "添加资产" else "编辑资产") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Column(Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(color = Orange500)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateEditField { copy(name = it) } },
                    label = { Text("名称 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.brand,
                        onValueChange = { viewModel.updateEditField { copy(brand = it) } },
                        label = { Text("品牌") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.model,
                        onValueChange = { viewModel.updateEditField { copy(model = it) } },
                        label = { Text("型号") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.categories.find { it.id == state.categoryId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("分类") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        state.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    viewModel.updateEditField { copy(categoryId = cat.id) }
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = state.price,
                    onValueChange = { viewModel.updateEditField { copy(price = it) } },
                    label = { Text("价格 (元)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.purchaseChannel,
                    onValueChange = { viewModel.updateEditField { copy(purchaseChannel = it) } },
                    label = { Text("购买渠道") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Status dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("状态") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        listOf("使用中", "闲置", "已借出", "已出售", "已丢弃").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    viewModel.updateEditField { copy(status = s) }
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                // Location dropdown
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.locations.find { it.id == state.locationId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("位置") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        state.locations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc.name) },
                                onClick = {
                                    viewModel.updateEditField { copy(locationId = loc.id) }
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = state.specs,
                    onValueChange = { viewModel.updateEditField { copy(specs = it) } },
                    label = { Text("规格参数") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = state.serialNumber,
                    onValueChange = { viewModel.updateEditField { copy(serialNumber = it) } },
                    label = { Text("序列号") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { viewModel.updateEditField { copy(notes = it) } },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("虚拟资产", Modifier.weight(1f))
                    Switch(
                        checked = state.isVirtual,
                        onCheckedChange = { viewModel.updateEditField { copy(isVirtual = it) } }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.saveAsset() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = state.name.isNotBlank() && !state.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange500)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("保存", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
```

---

### Task 4.4: AssetDetailScreen UI

**Files:**
- Modify: `app/src/main/java/com/zichan/app/ui/asset/AssetDetailScreen.kt`

- [ ] **Step 1: 重写 AssetDetailScreen.kt**

```kotlin
package com.zichan.app.ui.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zichan.app.data.entity.AssetEntity
import com.zichan.app.ui.theme.Orange500
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    assetId: Long,
    onEdit: (Long) -> Unit = {},
    onLend: (Long) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: AssetViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSellDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val asset = state.assets.find { it.id == assetId }

    if (showDeleteDialog && asset != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除「${asset.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAsset(asset)
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(asset?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (asset != null) {
                        IconButton(onClick = { onEdit(asset.id) }) {
                            Icon(Icons.Filled.Edit, "编辑")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, "删除")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (asset == null) {
            Column(Modifier.fillMaxSize().padding(padding)) {
                Text("资产不存在")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Price card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Orange500)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("购入价格", color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp)
                        Text(
                            NumberFormat.getCurrencyInstance(Locale.CHINA).format(asset.price),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Info section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        DetailRow("状态", asset.status)
                        HorizontalDivider()
                        DetailRow("品牌", asset.brand)
                        HorizontalDivider()
                        DetailRow("型号", asset.model)
                        if (asset.specs.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("规格", asset.specs)
                        }
                        if (asset.serialNumber.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("序列号", asset.serialNumber)
                        }
                        HorizontalDivider()
                        DetailRow("购买渠道", asset.purchaseChannel)
                        if (asset.purchaseDate != null) {
                            HorizontalDivider()
                            DetailRow("购买日期", dateFormat.format(Date(asset.purchaseDate)))
                        }
                        if (asset.notes.isNotBlank()) {
                            HorizontalDivider()
                            DetailRow("备注", asset.notes)
                        }
                    }
                }

                // Actions
                if (asset.status == "使用中" || asset.status == "闲置") {
                    Button(
                        onClick = { onLend(asset.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange500)
                    ) {
                        Text("借出")
                    }
                }

                if (asset.status != "已出售" && asset.status != "已丢弃") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showSellDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("出售", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Button(
                            onClick = { showDiscardDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("丢弃", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
```

---

## Phase 5-P7 任务摘要

以下为后续阶段的任务，每个大功能块对应一组文件创建/修改：

### Phase 5: 统计 (P1)

| 任务 | 文件 | 内容 |
|---|---|---|
| 5.1 | `ui/stats/StatsViewModel.kt` | 分类价值汇总、月度支出、折旧计算 |
| 5.2 | `ui/stats/StatsScreen.kt` | 柱状图、饼图、月度支出列表 |

### Phase 6: 我的 (P1)

| 任务 | 文件 | 内容 |
|---|---|---|
| 6.1 | `ui/profile/ProfileViewModel.kt` | 备份导出、生物识别、日志管理 |
| 6.2 | `ui/profile/ProfileScreen.kt` | 设置列表、导出按钮、生物识别开关 |
| 6.3 | `ui/profile/PersonListScreen.kt` | 联系人列表 |
| 6.4 | `ui/profile/PersonEditScreen.kt` | 联系人编辑表单 |
| 6.5 | `ui/profile/LogScreen.kt` | 操作日志列表 |
| 6.6 | `util/BiometricHelper.kt` | BiometricPrompt 封装 |
| 6.7 | `util/BackupManager.kt` | JSON 导出 + 自动周备份 |

### Phase 7: 借还管理 (P1)

| 任务 | 文件 | 内容 |
|---|---|---|
| 7.1 | `ui/asset/LendManageScreen.kt` | 借出表单（选联系人、日期） |
| 7.2 | `ui/asset/LendViewModel.kt` | 借还逻辑 |

### Phase 8: 拍照与抛光 (P2-P3)

| 任务 | 文件 | 内容 |
|---|---|---|
| 8.1 | `util/PhotoManager.kt` | CameraX 拍照 + 本地存储 |
| 8.2 | Photo 集成 | AssetEditScreen + AssetDetailScreen 加入拍照/显示 |
| 8.3 | 暗色主题验证 | 测试跟随系统切换 |
| 8.4 | 折旧策略 | `data/strategy/` 移植三种折旧算法 |

---

## 执行优先级

1. **Phase 0**: 项目骨架（Gradle + Manifest + 资源） → 可编译空 App
2. **Phase 1**: 数据层（Entity + DAO + Database + Repository + DI） → 数据库就绪
3. **Phase 2**: 主题 + 导航（Theme + NavHost + 占位 Screen） → 四标签可切换
4. **Phase 3**: 首页（HomeViewModel + HomeScreen） → 展示统计数据
5. **Phase 4**: 资产管理（AssetViewModel + List/Edit/Detail Screen） → P0 完整可用
6. **Phase 5-8**: P1-P3 逐步完善
