package com.example.zensqlite.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zensqlite.ZenApplication
import com.example.zensqlite.data.entity.ProductEntity
import com.example.zensqlite.utils.ImageUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductUiState(
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository = (application as ZenApplication).productRepository
    private val context = application.applicationContext

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val products: StateFlow<List<ProductEntity>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        when {
            query.isNotBlank() -> productRepository.searchProducts(query)
            category != null -> productRepository.getProductsByCategory(category)
            else -> productRepository.allProducts
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalProducts: StateFlow<Int> = productRepository.totalProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStock: StateFlow<Int> = productRepository.totalStock
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalInventoryValue: StateFlow<Double> = productRepository.totalInventoryValue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categories: StateFlow<List<String>> = productRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun addProduct(
        productCode: String,
        name: String,
        category: String,
        quantity: String,
        price: String,
        imageUri: Uri?
    ) {
        when {
            productCode.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Kode produk harus diisi")
                return
            }
            name.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Nama produk harus diisi")
                return
            }
            category.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Kategori harus diisi")
                return
            }
            quantity.isBlank() || quantity.toIntOrNull() == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Kuantitas harus berupa angka valid")
                return
            }
            price.isBlank() || price.toDoubleOrNull() == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Harga harus berupa angka valid")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val imagePath = imageUri?.let { ImageUtils.saveImageToInternalStorage(context, it) }

            val product = ProductEntity(
                productCode = productCode.trim(),
                name = name.trim(),
                category = category.trim(),
                quantity = quantity.toInt(),
                price = price.toDouble(),
                imagePath = imagePath
            )

            val result = productRepository.insertProduct(product)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveSuccess = true,
                    successMessage = "Produk berhasil ditambahkan"
                )
            }.onFailure { error ->
                if (imagePath != null) ImageUtils.deleteImage(imagePath)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Gagal menambahkan produk"
                )
            }
        }
    }

    fun updateProduct(
        existingProduct: ProductEntity,
        productCode: String,
        name: String,
        category: String,
        quantity: String,
        price: String,
        imageUri: Uri?,
        imageChanged: Boolean
    ) {
        when {
            productCode.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Kode produk harus diisi")
                return
            }
            name.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Nama produk harus diisi")
                return
            }
            category.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Kategori harus diisi")
                return
            }
            quantity.isBlank() || quantity.toIntOrNull() == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Kuantitas harus berupa angka valid")
                return
            }
            price.isBlank() || price.toDoubleOrNull() == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Harga harus berupa angka valid")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            var newImagePath = existingProduct.imagePath
            if (imageChanged) {
                if (imageUri != null) {
                    newImagePath = ImageUtils.saveImageToInternalStorage(context, imageUri)
                    if (existingProduct.imagePath != null) {
                        ImageUtils.deleteImage(existingProduct.imagePath)
                    }
                } else {
                    if (existingProduct.imagePath != null) {
                        ImageUtils.deleteImage(existingProduct.imagePath)
                    }
                    newImagePath = null
                }
            }

            val updatedProduct = existingProduct.copy(
                productCode = productCode.trim(),
                name = name.trim(),
                category = category.trim(),
                quantity = quantity.toInt(),
                price = price.toDouble(),
                imagePath = newImagePath
            )

            val result = productRepository.updateProduct(updatedProduct)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveSuccess = true,
                    successMessage = "Produk berhasil diperbarui"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Gagal memperbarui produk"
                )
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = productRepository.deleteProduct(product)
            result.onSuccess {
                ImageUtils.deleteImage(product.imagePath)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    deleteSuccess = true,
                    successMessage = "Produk berhasil dihapus"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Gagal menghapus produk"
                )
            }
        }
    }

    suspend fun getProductById(id: Long): ProductEntity? {
        return productRepository.getProductById(id)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            saveSuccess = false,
            deleteSuccess = false
        )
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun resetDeleteSuccess() {
        _uiState.value = _uiState.value.copy(deleteSuccess = false)
    }
}
