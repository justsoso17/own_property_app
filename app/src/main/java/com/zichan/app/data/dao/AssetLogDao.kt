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
