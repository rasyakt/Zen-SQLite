package com.example.zensqlite

import android.app.Application
import com.example.zensqlite.data.database.AppDatabase
import com.example.zensqlite.data.repository.ProductRepository
import com.example.zensqlite.data.repository.UserRepository

class ZenApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository(database.productDao())
    }
}
