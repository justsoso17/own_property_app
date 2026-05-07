package com.zichan.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lend_records",
    foreignKeys = [
        ForeignKey(
            entity = AssetEntity::class,
            parentColumns = ["id"],
            childColumns = ["asset_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.SET_NULL
        )
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
