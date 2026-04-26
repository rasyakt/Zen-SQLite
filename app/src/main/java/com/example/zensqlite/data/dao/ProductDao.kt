package com.example.zensqlite.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zensqlite.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductByIdFlow(id: Long): Flow<ProductEntity?>

    @Query("""
        SELECT * FROM products 
        WHERE name LIKE '%' || :query || '%' 
        OR productCode LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY updatedAt DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM products")
    fun getTotalProducts(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM products")
    fun getTotalStock(): Flow<Int>

    @Query("SELECT COALESCE(SUM(price * quantity), 0.0) FROM products")
    fun getTotalInventoryValue(): Flow<Double>

    @Query("SELECT EXISTS(SELECT 1 FROM products WHERE productCode = :code)")
    suspend fun isProductCodeExists(code: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM products WHERE productCode = :code AND id != :excludeId)")
    suspend fun isProductCodeExistsExcluding(code: String, excludeId: Long): Boolean
}
