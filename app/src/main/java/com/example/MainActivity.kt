package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.InventoryRepository
import com.example.ui.components.BottomNavSection
import com.example.ui.components.HeaderSection
import com.example.ui.components.ProductDetailSheet
import com.example.ui.components.RegisterProductSheet
import com.example.ui.components.StockOutSheet
import com.example.ui.components.ToastPill
import com.example.ui.screens.ProductsTabScreen
import com.example.ui.screens.ReportsTabScreen
import com.example.ui.screens.StockTabScreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.ShelfInventoryTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BottomSheetState
import com.example.ui.viewmodel.InventoryViewModel
import com.example.ui.viewmodel.InventoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShelfInventoryTheme {
                val context = LocalContext.current
                val database = AppDatabase.getDatabase(context)
                val repository = InventoryRepository(database.productDao(), database.logDao())
                val factory = InventoryViewModelFactory(repository)
                val viewModel: InventoryViewModel = viewModel(factory = factory)

                ShelfInventoryApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ShelfInventoryApp(
    viewModel: InventoryViewModel
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val reportFilter by viewModel.reportFilter.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val showDeleteConfirm by viewModel.showDeleteConfirm.collectAsStateWithLifecycle()

    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val filteredLogs by viewModel.filteredLogs.collectAsStateWithLifecycle()
    val selectedProductLogs by viewModel.selectedProductLogs.collectAsStateWithLifecycle()

    val totalProductsCount = allProducts.size
    val totalQtySum = allProducts.sumOf { it.qty }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0B0F1A),
            BgDark
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                HeaderSection(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.switchTab(it) }
                )

                // Main Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentTab) {
                        AppTab.STOCK -> {
                            StockTabScreen(
                                totalProductsCount = totalProductsCount,
                                totalQtySum = totalQtySum,
                                lowStockProducts = lowStockProducts,
                                onProductClick = { viewModel.openDetailSheet(it) }
                            )
                        }
                        AppTab.PRODUCTS -> {
                            ProductsTabScreen(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                products = filteredProducts,
                                onProductClick = { viewModel.openDetailSheet(it) }
                            )
                        }
                        AppTab.REPORTS -> {
                            ReportsTabScreen(
                                currentFilter = reportFilter,
                                onFilterChange = { viewModel.setReportFilter(it) },
                                logs = filteredLogs
                            )
                        }
                    }
                }
            }

            // Fixed Bottom Navigation Bar
            BottomNavSection(
                selectedTab = currentTab,
                onTabSelected = { viewModel.switchTab(it) },
                onRegisterClick = { viewModel.openRegisterSheet() },
                onStockOutClick = {
                    if (allProducts.isEmpty()) {
                        viewModel.showToast("Weli alaab lama diiwaan gelin")
                    } else {
                        viewModel.openStockOutSheet()
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Sheet Overlays
            when (val sheet = activeSheet) {
                is BottomSheetState.Register -> {
                    RegisterProductSheet(
                        onDismiss = { viewModel.closeSheet() },
                        onSave = { name, qty, shelf ->
                            viewModel.saveProduct(name, qty, shelf)
                        }
                    )
                }
                is BottomSheetState.Detail -> {
                    val detailProduct = allProducts.find { it.id == sheet.productId }
                    ProductDetailSheet(
                        product = detailProduct,
                        historyLogs = selectedProductLogs,
                        showDeleteConfirm = showDeleteConfirm,
                        onDismiss = { viewModel.closeSheet() },
                        onIncreaseQty = { amount ->
                            viewModel.increaseQty(sheet.productId, amount)
                        },
                        onOpenStockOut = {
                            viewModel.openStockOutSheet(preselectedProductId = sheet.productId)
                        },
                        onRequestDelete = { viewModel.requestDeleteProduct() },
                        onCancelDelete = { viewModel.cancelDeleteProduct() },
                        onConfirmDelete = { viewModel.confirmDeleteProduct(it) }
                    )
                }
                is BottomSheetState.StockOut -> {
                    StockOutSheet(
                        products = allProducts,
                        preselectedProductId = sheet.preselectedProductId,
                        onDismiss = { viewModel.closeSheet() },
                        onConfirm = { productId, qty ->
                            viewModel.confirmStockOut(productId, qty)
                        }
                    )
                }
                is BottomSheetState.None -> { /* No sheet */ }
            }

            // Toast overlay
            ToastPill(
                message = toastMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
            )
        }
    }
}
