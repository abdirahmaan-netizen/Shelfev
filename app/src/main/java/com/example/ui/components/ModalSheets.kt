package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Product
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentCyanAlpha15
import com.example.ui.theme.BorderDark
import com.example.ui.theme.Card2Dark
import com.example.ui.theme.CardDark
import com.example.ui.theme.Color0B0F1A
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedAlpha15
import com.example.ui.theme.DangerRedAlpha30
import com.example.ui.theme.OverlayBg
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarnAmber
import com.example.ui.theme.WarnAmberAlpha15
import com.example.ui.theme.WarnAmberAlpha30
import com.example.ui.viewmodel.LogWithBalance
import com.example.util.DateUtils

@Composable
fun SheetOverlayContainer(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBg)
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(CardDark)
                .clickable(enabled = false) {} // prevent click through
                .padding(top = 20.dp, start = 18.dp, end = 18.dp, bottom = 24.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Card2Dark)
            .border(1.dp, BorderDark, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✕",
            fontSize = 16.sp,
            color = TextMuted
        )
    }
}

@Composable
fun FormFieldInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Card2Dark)
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = TextMuted,
                    fontSize = 15.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextLight,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(TextLight),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 1. REGISTER PRODUCT SHEET
@Composable
fun RegisterProductSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, qty: String, shelf: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var shelf by remember { mutableStateOf("") }

    SheetOverlayContainer(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = "Diiwaan geli alaab cusub",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Buuxi macluumaadka hoose",
                        fontSize = 12.5.sp,
                        color = TextMuted
                    )
                }
                CloseIconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            FormFieldInput(
                label = "Magaca alaabta (Name product)",
                value = name,
                onValueChange = { name = it },
                placeholder = "Tusaale: Amoxicillin 500mg"
            )

            FormFieldInput(
                label = "Tirada (Quantity)",
                value = qty,
                onValueChange = { qty = it },
                placeholder = "Tusaale: 50",
                keyboardType = KeyboardType.Number
            )

            FormFieldInput(
                label = "Nambarka Shelf-ka (Shelf number)",
                value = shelf,
                onValueChange = { shelf = it },
                placeholder = "Tusaale: A1 ama Shelf-03"
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cancel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Card2Dark)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Cancel", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                }

                // Save
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGradient)
                        .clickable { onSave(name, qty, shelf) }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Save", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color0B0F1A)
                }
            }
        }
    }
}

// 2. DETAIL SHEET
@Composable
fun ProductDetailSheet(
    product: Product?,
    historyLogs: List<LogWithBalance>,
    showDeleteConfirm: Boolean,
    onDismiss: () -> Unit,
    onIncreaseQty: (amount: String) -> Unit,
    onOpenStockOut: () -> Unit,
    onRequestDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmDelete: (productId: Long) -> Unit
) {
    if (product == null) return

    var incAmount by remember { mutableStateOf("") }

    SheetOverlayContainer(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = product.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Shelf ${product.shelf}",
                        fontSize = 12.5.sp,
                        color = TextMuted
                    )
                }
                CloseIconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // Big Qty
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.qty.toString(),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentCyan
                )
                Text(
                    text = "Balance ee stock-ga",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Details rows
            DetailRow(key = "Shelf number", value = product.shelf)
            DetailRow(key = "Diiwaan la geliyay", value = DateUtils.formatDate(product.createdAt))

            // Increase Qty
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(
                    text = "Ku dar tiro (Increase quantity)",
                    fontSize = 12.5.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Card2Dark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        if (incAmount.isEmpty()) {
                            Text(text = "0", color = TextMuted, fontSize = 15.sp, textAlign = TextAlign.Center)
                        }
                        BasicTextField(
                            value = incAmount,
                            onValueChange = { incAmount = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = TextLight,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            ),
                            cursorBrush = SolidColor(TextLight),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentGradient)
                            .clickable {
                                onIncreaseQty(incAmount)
                                incAmount = ""
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "➕ Dar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color0B0F1A)
                    }
                }
            }

            // Action grid
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                // Stock Out (Bixi) full width
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarnAmberAlpha15)
                        .border(1.dp, WarnAmberAlpha30, RoundedCornerShape(12.dp))
                        .clickable { onOpenStockOut() }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📤 Stock Out (Bixi)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = WarnAmber)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Dabool
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Card2Dark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .clickable { onDismiss() }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Dabool", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    }

                    // Delete
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DangerRedAlpha15)
                            .border(1.dp, DangerRedAlpha30, RoundedCornerShape(12.dp))
                            .clickable { onRequestDelete() }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🗑 Delete", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DangerRed)
                    }
                }
            }

            // History section
            SectionTitle(title = "Taariikhda dhaqdhaqaaqa", modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))

            if (historyLogs.isEmpty()) {
                EmptyStateView(message = "Weli dhaqdhaqaaq lama diiwaan gelin.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                ) {
                    items(historyLogs, key = { it.log.id }) { item ->
                        HistoryRowItem(item = item)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = onCancelDelete,
            title = { Text(text = "Tirtir alaabta", color = TextLight) },
            text = { Text(text = "Ma hubtaa inaad tirtirto alaabtan?", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { onConfirmDelete(product.id) }) {
                    Text(text = "Tirtir", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelDelete) {
                    Text(text = "Kansal", color = TextMuted)
                }
            },
            containerColor = CardDark
        )
    }
}

@Composable
private fun DetailRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.dp, color = BorderDark)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = key, fontSize = 14.sp, color = TextMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
    }
}

@Composable
private fun HistoryRowItem(item: LogWithBalance) {
    val isIn = item.log.type == "in"
    val iconBg = if (isIn) AccentCyanAlpha15 else DangerRedAlpha15
    val iconText = if (isIn) "⬇️" else "⬆️"
    val typeTitle = if (isIn) "Stock In" else "Stock Out"
    val textColor = if (isIn) AccentCyan else DangerRed
    val amountSign = if (isIn) "+" else "-"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Card2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconText, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = typeTitle, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = DateUtils.formatDate(item.log.timestamp), fontSize = 11.sp, color = TextMuted)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "$amountSign${item.log.amount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Balance: ${item.runningBalance}", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

// 3. STOCK OUT SHEET
@Composable
fun StockOutSheet(
    products: List<Product>,
    preselectedProductId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (productId: Long, qty: String) -> Unit
) {
    if (products.isEmpty()) return

    val sortedProducts = remember(products) { products.sortedBy { it.name.lowercase() } }
    val initialSelected = remember(sortedProducts, preselectedProductId) {
        sortedProducts.find { it.id == preselectedProductId } ?: sortedProducts.first()
    }

    var selectedProduct by remember { mutableStateOf(initialSelected) }
    var qty by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    SheetOverlayContainer(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(text = "Stock Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${selectedProduct.name} — Shelf ${selectedProduct.shelf}",
                        fontSize = 12.5.sp,
                        color = TextMuted
                    )
                }
                CloseIconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // Product Dropdown / Picker
            Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                Text(
                    text = "Dooro alaabta (haddii aan hore loo dooran)",
                    fontSize = 12.5.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Card2Dark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "${selectedProduct.name} (Shelf ${selectedProduct.shelf})",
                            color = TextLight,
                            fontSize = 15.sp
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier
                            .background(Card2Dark)
                            .border(1.dp, BorderDark)
                    ) {
                        sortedProducts.forEach { prod ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${prod.name} (Shelf ${prod.shelf})",
                                        color = TextLight,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    selectedProduct = prod
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Quantity input
            FormFieldInput(
                label = "Tirada la bixinayo (Quantity out)",
                value = qty,
                onValueChange = { qty = it },
                placeholder = "0",
                keyboardType = KeyboardType.Number
            )

            Text(
                text = "Stock hadda: ${selectedProduct.qty} halkii",
                fontSize = 12.5.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cancel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Card2Dark)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Cancel", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                }

                // Confirm
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarnAmberAlpha15)
                        .border(1.dp, WarnAmberAlpha30, RoundedCornerShape(12.dp))
                        .clickable { onConfirm(selectedProduct.id, qty) }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Xaqiiji Bixinta", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = WarnAmber)
                }
            }
        }
    }
}
