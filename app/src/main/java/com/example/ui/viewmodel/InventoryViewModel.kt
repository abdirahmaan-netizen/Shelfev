package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.LogEntry
import com.example.data.entity.Product
import com.example.data.repository.InventoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    STOCK, PRODUCTS, REPORTS
}

enum class ReportFilter {
    ALL, IN, OUT
}

sealed class BottomSheetState {
    object None : BottomSheetState()
    object Register : BottomSheetState()
    data class Detail(val productId: Long) : BottomSheetState()
    data class StockOut(val preselectedProductId: Long? = null) : BottomSheetState()
}

data class LogWithBalance(
    val log: LogEntry,
    val runningBalance: Int
)

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppTab.STOCK)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _reportFilter = MutableStateFlow(ReportFilter.ALL)
    val reportFilter: StateFlow<ReportFilter> = _reportFilter.asStateFlow()

    private val _activeSheet = MutableStateFlow<BottomSheetState>(BottomSheetState.None)
    val activeSheet: StateFlow<BottomSheetState> = _activeSheet.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _showDeleteConfirm = MutableStateFlow(false)
    val showDeleteConfirm: StateFlow<Boolean> = _showDeleteConfirm.asStateFlow()

    val allProducts: StateFlow<List<Product>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val lowStockProducts: StateFlow<List<Product>> = allProducts.map { list ->
        list.filter { it.qty <= 5 }.sortedBy { it.qty }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredProducts: StateFlow<List<Product>> = combine(allProducts, searchQuery) { list, query ->
        if (query.isBlank()) {
            list.sortedByDescending { it.createdAt }
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) }
                .sortedByDescending { it.createdAt }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allLogs: StateFlow<List<LogEntry>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredLogs: StateFlow<List<LogEntry>> = combine(allLogs, reportFilter) { logs, filter ->
        val sorted = logs.sortedByDescending { it.timestamp }
        when (filter) {
            ReportFilter.ALL -> sorted.take(200)
            ReportFilter.IN -> sorted.filter { it.type == "in" }.take(200)
            ReportFilter.OUT -> sorted.filter { it.type == "out" }.take(200)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedProductLogs: StateFlow<List<LogWithBalance>> = activeSheet.flatMapLatest { sheet ->
        if (sheet is BottomSheetState.Detail) {
            repository.getLogsForProduct(sheet.productId).map { logs ->
                // Sort oldest -> newest to calculate running balance correctly
                val sortedAsc = logs.sortedBy { it.timestamp }
                var running = 0
                val result = sortedAsc.map { l ->
                    running += if (l.type == "in") l.amount else -l.amount
                    LogWithBalance(l, running)
                }
                result.reversed() // show newest first
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun switchTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setReportFilter(filter: ReportFilter) {
        _reportFilter.value = filter
    }

    fun openRegisterSheet() {
        _activeSheet.value = BottomSheetState.Register
    }

    fun openDetailSheet(productId: Long) {
        _activeSheet.value = BottomSheetState.Detail(productId)
    }

    fun openStockOutSheet(preselectedProductId: Long? = null) {
        _activeSheet.value = BottomSheetState.StockOut(preselectedProductId)
    }

    fun closeSheet() {
        _activeSheet.value = BottomSheetState.None
        _showDeleteConfirm.value = false
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.value = msg
            delay(2200)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun saveProduct(name: String, qtyStr: String, shelf: String) {
        val trimmedName = name.trim()
        val qty = qtyStr.toIntOrNull()
        val trimmedShelf = shelf.trim()

        if (trimmedName.isEmpty()) {
            showToast("Fadlan geli magaca alaabta")
            return
        }
        if (qty == null || qty < 0) {
            showToast("Fadlan geli tiro sax ah")
            return
        }
        if (trimmedShelf.isEmpty()) {
            showToast("Fadlan geli nambarka shelf-ka")
            return
        }

        viewModelScope.launch {
            repository.addProduct(trimmedName, qty, trimmedShelf)
            closeSheet()
            showToast("Alaabta waa la diiwaan geliyay ✅")
        }
    }

    fun increaseQty(productId: Long, amountStr: String) {
        val amount = amountStr.toIntOrNull()
        if (amount == null || amount <= 0) {
            showToast("Fadlan geli tiro sax ah")
            return
        }

        viewModelScope.launch {
            val success = repository.increaseQty(productId, amount)
            if (success) {
                showToast("Tirada waa la kordhiyay ✅")
            } else {
                showToast("Khalad ayaa dhacay")
            }
        }
    }

    fun confirmStockOut(productId: Long, qtyStr: String) {
        val qty = qtyStr.toIntOrNull()
        if (qty == null || qty <= 0) {
            showToast("Fadlan geli tiro sax ah")
            return
        }

        viewModelScope.launch {
            val result = repository.stockOut(productId, qty)
            result.onSuccess {
                showToast("Stock Out waa la xaqiijiyay 📤")
                closeSheet()
            }.onFailure { ex ->
                showToast(ex.message ?: "Khalad ayaa dhacay")
            }
        }
    }

    fun requestDeleteProduct() {
        _showDeleteConfirm.value = true
    }

    fun cancelDeleteProduct() {
        _showDeleteConfirm.value = false
    }

    fun confirmDeleteProduct(productId: Long) {
        viewModelScope.launch {
            val success = repository.deleteProduct(productId)
            if (success) {
                showToast("Alaabta waa la tirtiray 🗑")
                closeSheet()
            } else {
                showToast("Khalad ayaa dhacay")
            }
        }
    }
}

class InventoryViewModelFactory(
    private val repository: InventoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
