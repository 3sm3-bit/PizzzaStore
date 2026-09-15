package com.pizzza.pizzzastore.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pizzza.pizzzastore.repository.db.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("DELETE FROM products WHERE uid = :id")
    suspend fun deleteById(id: String)
}