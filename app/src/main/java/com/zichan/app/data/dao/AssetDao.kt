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

    @Query(
        "SELECT * FROM assets WHERE " +
        "name LIKE '%' || :keyword || '%' OR " +
        "brand LIKE '%' || :keyword || '%' OR " +
        "model LIKE '%' || :keyword || '%' OR " +
        "notes LIKE '%' || :keyword || '%' " +
        "ORDER BY created_at DESC"
    )
    fun search(keyword: String): Flow<List<AssetEntity>>

    @Query(
        "SELECT * FROM assets WHERE " +
        "(:keyword IS NULL OR name LIKE '%' || :keyword || '%' OR brand LIKE '%' || :keyword || '%') AND " +
        "(:categoryId IS NULL OR category_id = :categoryId) AND " +
        "(:status IS NULL OR status = :status) AND " +
        "(:minPrice IS NULL OR price >= :minPrice) AND " +
        "(:maxPrice IS NULL OR price <= :maxPrice) " +
        "ORDER BY created_at DESC"
    )
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

    @Query(
        "SELECT * FROM assets WHERE is_virtual = 1 AND " +
        "expiry_date IS NOT NULL AND " +
        "expiry_date BETWEEN :now AND :sevenDaysLater"
    )
    fun getExpiringSoon(now: Long, sevenDaysLater: Long): Flow<List<AssetEntity>>
}
