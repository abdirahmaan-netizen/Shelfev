package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Product
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCardItem
import com.example.ui.components.SectionTitle
import com.example.ui.theme.BorderDark
import com.example.ui.theme.Card2Dark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun StockTabScreen(
    totalProductsCount: Int,
    totalQtySum: Int,
    lowStockProducts: List<Product>,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        item {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                StatCard(
                    number = totalProductsCount.toString(),
                    label = "Alaabta guud",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatCard(
                    number = totalQtySum.toString(),
                    label = "Tirada guud (Stock)",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle(title = "Ku dhawaad dhamaad (Stock hooseeya)")
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (lowStockProducts.isEmpty()) {
            item {
                EmptyStateView(message = "Alaab stock-eeda hooseeyaa ma jirto 👍")
            }
        } else {
            items(lowStockProducts, key = { it.id }) { product ->
                ProductCardItem(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
        }
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Card2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = number,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
