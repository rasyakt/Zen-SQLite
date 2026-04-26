package com.example.zensqlite.data.repository

import com.example.zensqlite.data.dao.ProductDao
import com.example.zensqlite.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    val totalProducts: Flow<Int> = productDao.getTotalProducts()

    val totalStock: Flow<Int> = productDao.getTotalStock()

    val totalInventoryValue: Flow<Double> = productDao.getTotalInventoryValue()

    val allCategories: Flow<List<String>> = productDao.getAllCategories()

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return productDao.searchProducts(query)
    }

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
        return productDao.getProductsByCategory(category)
    }

    fun getProductByIdFlow(id: Long): Flow<ProductEntity?> {
        return productDao.getProductByIdFlow(id)
    }

    suspend fun getProductById(id: Long): ProductEntity? {
        return productDao.getProductById(id)
    }

    suspend fun insertProduct(product: ProductEntity): Result<Long> {
        return try {
            val exists = productDao.isProductCodeExists(product.productCode)
            if (exists) {
                Result.failure(Exception("Kode produk sudah digunakan"))
            } else {
                val id = productDao.insertProduct(product)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: ProductEntity): Result<Unit> {
        return try {
            val exists = productDao.isProductCodeExistsExcluding(product.productCode, product.id)
            if (exists) {
                Result.failure(Exception("Kode produk sudah digunakan oleh produk lain"))
            } else {
                productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(product: ProductEntity): Result<Unit> {
        return try {
            productDao.deleteProduct(product)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
