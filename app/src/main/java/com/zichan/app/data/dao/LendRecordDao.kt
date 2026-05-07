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
