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
