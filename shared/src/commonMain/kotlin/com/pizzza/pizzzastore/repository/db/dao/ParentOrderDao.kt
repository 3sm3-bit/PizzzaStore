package com.pizzza.pizzzastore.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pizzza.pizzzastore.repository.db.entity.ParentOrderEntity

@Dao
interface ParentOrderDao {
    @Query("SELECT * FROM parent_orders")
    suspend fun getAll(): List<ParentOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<ParentOrderEntity>)

    @Query("DELETE FROM parent_orders")
    suspend fun deleteAll()
}