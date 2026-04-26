package com.example.zensqlite.ui.screens.product

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.zensqlite.data.entity.ProductEntity
import com.example.zensqlite.ui.theme.CoralRed
import com.example.zensqlite.ui.theme.DarkNavy
import com.example.zensqlite.ui.theme.LightBackground
import com.example.zensqlite.ui.theme.RoyalBlue
import com.example.zensqlite.ui.theme.SuccessGreen
import com.example.zensqlite.ui.theme.TextSecondary
import com.example.zensqlite.ui.theme.WarningAmber
import com.example.zensqlite.ui.viewmodel.ProductViewModel
import com.example.zensqlite.utils.CurrencyUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productViewModel: ProductViewModel,
    productId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uiState by productViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        product = productViewModel.getProductById(productId)
    }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            productViewModel.resetDeleteSuccess()
            onNavigateBack()
        }
    }

    // Delete Confirmation
    if (showDeleteDialog && product != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Hapus Produk",
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavy
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus \"${product!!.name}\"? Tindakan ini tidak bisa dibatalkan.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    productViewModel.deleteProduct(product!!)
                }) {
                    Text("Hapus", color = CoralRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Produk",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkNavy,
                    navigationIconContentColor = DarkNavy
                )
            )
        },
        containerColor = LightBackground
    ) { paddingValues ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DarkNavy)
            }
        } else {
            val p = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (p.imagePath != null && File(p.imagePath).exists()) {
                        AsyncImage(
                            model = File(p.imagePath),
                            contentDescription = p.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tidak ada foto",
                                fontSize = 13.sp,
                                color = TextSecondary.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Product Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Name & Category
                        Text(
                            text = p.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    RoyalBlue.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = p.category,
                                fontSize = 13.sp,
                                color = RoyalBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Price
                        Text(
                            text = CurrencyUtils.formatRupiah(p.price),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralRed
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Info Rows
                        DetailInfoRow(
                            icon = Icons.Outlined.QrCode,
                            label = "Kode Produk",
                            value = p.productCode
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailInfoRow(
                            icon = Icons.Outlined.Category,
                            label = "Kategori",
                            value = p.category
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailInfoRow(
                            icon = Icons.Outlined.Inventory,
                            label = "Stok Tersedia",
                            value = "${p.quantity} unit",
                            valueColor = if (p.quantity > 10) SuccessGreen
                            else if (p.quantity > 0) WarningAmber
                            else CoralRed
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val totalValue = p.price * p.quantity
                        DetailInfoRow(
                            icon = Icons.Outlined.Inventory2,
                            label = "Total Nilai",
                            value = CurrencyUtils.formatRupiah(totalValue)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Timestamps
                        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id"))
                        Text(
                            text = "Dibuat: ${dateFormat.format(Date(p.createdAt))}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Diperbarui: ${dateFormat.format(Date(p.updatedAt))}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CoralRed
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Hapus",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { onNavigateToEdit(p.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkNavy
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Edit",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = DarkNavy
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(LightBackground, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}
