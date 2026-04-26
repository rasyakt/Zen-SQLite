package com.example.zensqlite.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zensqlite.data.entity.ProductEntity
import com.example.zensqlite.ui.theme.DarkBlue
import com.example.zensqlite.ui.theme.PrimaryBlue
import com.example.zensqlite.ui.theme.LightBlue
import com.example.zensqlite.ui.theme.AppBackground
import com.example.zensqlite.ui.theme.ErrorRed
import com.example.zensqlite.ui.theme.InfoBlue
import com.example.zensqlite.ui.theme.SuccessGreen
import com.example.zensqlite.ui.theme.TextSecondary
import com.example.zensqlite.ui.theme.WarningAmber
import com.example.zensqlite.ui.viewmodel.AuthViewModel
import com.example.zensqlite.ui.viewmodel.ProductViewModel
import com.example.zensqlite.utils.CurrencyUtils
import java.io.File

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val products by productViewModel.products.collectAsState()
    val totalProducts by productViewModel.totalProducts.collectAsState()
    val totalStock by productViewModel.totalStock.collectAsState()
    val totalValue by productViewModel.totalInventoryValue.collectAsState()
    val categories by productViewModel.categories.collectAsState()
    val searchQuery by productViewModel.searchQuery.collectAsState()
    val selectedCategory by productViewModel.selectedCategory.collectAsState()
    val productUiState by productViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(productUiState.deleteSuccess) {
        if (productUiState.deleteSuccess) {
            snackbarHostState.showSnackbar(productUiState.successMessage ?: "Berhasil")
            productViewModel.resetDeleteSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        },
        containerColor = AppBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Halo,",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = authState.currentUser?.fullName ?: "User",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.15f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Summary Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "Produk",
                                value = totalProducts.toString(),
                                icon = Icons.Outlined.Widgets,
                                iconTint = InfoBlue
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "Stok",
                                value = totalStock.toString(),
                                icon = Icons.Outlined.Inventory,
                                iconTint = SuccessGreen
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "Nilai",
                                value = CurrencyUtils.formatRupiahCompact(totalValue),
                                icon = Icons.Outlined.AttachMoney,
                                iconTint = WarningAmber
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { productViewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Cari produk atau kode...",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { productViewModel.setSearchQuery("") }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = PrimaryBlue
                    ),
                    singleLine = true
                )
            }

            // Category Filter
            if (categories.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            SuggestionChip(
                                onClick = { productViewModel.setSelectedCategory(null) },
                                label = {
                                    Text(
                                        "Semua",
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedCategory == null) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (selectedCategory == null) Color.White else TextSecondary
                                    )
                                },
                                icon = {
                                    Icon(
                                        Icons.Outlined.Category,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (selectedCategory == null) PrimaryBlue else Color.White,
                                    labelColor = if (selectedCategory == null) Color.White else TextSecondary,
                                    iconContentColor = if (selectedCategory == null) Color.White else TextSecondary
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = if (selectedCategory == null) PrimaryBlue else Color(0xFFE5E7EB)
                                )
                            )
                        }
                        items(categories) { category ->
                            SuggestionChip(
                                onClick = {
                                    productViewModel.setSelectedCategory(
                                        if (selectedCategory == category) null else category
                                    )
                                },
                                label = {
                                    Text(
                                        category,
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedCategory == category) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (selectedCategory == category) Color.White else TextSecondary
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (selectedCategory == category) PrimaryBlue else Color.White,
                                    labelColor = if (selectedCategory == category) Color.White else TextSecondary
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = if (selectedCategory == category) PrimaryBlue else Color(0xFFE5E7EB)
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Produk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Text(
                        text = "${products.size} item",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Products or Empty State
            if (products.isEmpty()) {
                item {
                    EmptyState(
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory
                    )
                }
            } else {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onNavigateToProductDetail(product.id) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                if (product.imagePath != null && File(product.imagePath).exists()) {
                    AsyncImage(
                        model = File(product.imagePath),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Product Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.productCode,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .background(
                                LightBlue.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 11.sp,
                            color = DarkBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // Stock Badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (product.quantity > 10) SuccessGreen.copy(alpha = 0.1f)
                                else if (product.quantity > 0) WarningAmber.copy(alpha = 0.1f)
                                else ErrorRed.copy(alpha = 0.1f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Stok: ${product.quantity}",
                            fontSize = 11.sp,
                            color = if (product.quantity > 10) SuccessGreen
                            else if (product.quantity > 0) WarningAmber
                            else ErrorRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Price
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = CurrencyUtils.formatRupiah(product.price),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    searchQuery: String,
    selectedCategory: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            Icons.Outlined.Inventory2,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() -> "Tidak ditemukan"
                selectedCategory != null -> "Kategori kosong"
                else -> "Belum ada produk"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkBlue.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() -> "Coba kata kunci lain atau periksa ejaan"
                selectedCategory != null -> "Belum ada produk dalam kategori ini"
                else -> "Tambahkan produk pertama Anda\ndengan menekan tombol + di bawah"
            },
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
