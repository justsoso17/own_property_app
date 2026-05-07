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
    indices = [Index("category_id"), Index("location_id"), Index("status")]
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
