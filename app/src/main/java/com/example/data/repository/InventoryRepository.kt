package com.example.data.repository

import com.example.data.dao.LogDao
import com.example.data.dao.ProductDao
import com.example.data.entity.LogEntry
import com.example.data.entity.Product
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val productDao: ProductDao,
    private val logDao: LogDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allLogs: Flow<List<LogEntry>> = logDao.getAllLogs()

    suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)
    }

    fun getLogsForProduct(productId: Long): Flow<List<LogEntry>> {
        return logDao.getLogsForProduct(productId)
    }

    suspend fun addProduct(name: String, qty: Int, shelf: String): Long {
        val now = System.currentTimeMillis()
        val product = Product(
            name = name,
            qty = qty,
            shelf = shelf,
            createdAt = now
        )
        val productId = productDao.insertProduct(product)
        
        val log = LogEntry(
            type = "in",
            productId = productId,
            productName = name,
            amount = qty,
            note = "Diiwaan gelin cusub",
            timestamp = now
        )
        logDao.insertLog(log)
        return productId
    }

    suspend fun increaseQty(productId: Long, amount: Int): Boolean {
        val product = productDao.getProductById(productId) ?: return false
        val updatedProduct = product.copy(qty = product.qty + amount)
        productDao.updateProduct(updatedProduct)

        val log = LogEntry(
            type = "in",
            productId = productId,
            productName = product.name,
            amount = amount,
            note = "Kordhin tiro",
            timestamp = System.currentTimeMillis()
        )
        logDao.insertLog(log)
        return true
    }

    suspend fun stockOut(productId: Long, amount: Int): Result<Unit> {
        val product = productDao.getProductById(productId)
            ?: return Result.failure(Exception("Product not found"))
        if (amount > product.qty) {
            return Result.failure(Exception("Tirada aad rabto way ka badan tahay stock-ga hadda jira"))
        }

        val updatedProduct = product.copy(qty = product.qty - amount)
        productDao.updateProduct(updatedProduct)

        val log = LogEntry(
            type = "out",
            productId = productId,
            productName = product.name,
            amount = amount,
            note = "Stock Out",
            timestamp = System.currentTimeMillis()
        )
        logDao.insertLog(log)
        return Result.success(Unit)
    }

    suspend fun deleteProduct(productId: Long): Boolean {
        val product = productDao.getProductById(productId) ?: return false
        productDao.deleteProductById(productId)

        val log = LogEntry(
            type = "out",
            productId = productId,
            productName = product.name,
            amount = product.qty,
            note = "Alaabta waa la tirtiray",
            timestamp = System.currentTimeMillis()
        )
        logDao.insertLog(log)
        return true
    }
}
