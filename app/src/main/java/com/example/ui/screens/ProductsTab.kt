package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Product
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCardItem
import com.example.ui.theme.BorderDark
import com.example.ui.theme.Card2Dark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun ProductsTabScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    products: List<Product>,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Card2Dark)
                .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            if (searchQuery.isEmpty()) {
                Text(
                    text = "Raadi magaca alaabta...",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextLight,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(TextLight),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (products.isEmpty()) {
                item {
                    EmptyStateView(message = "Weli alaab lama diiwaan gelin.")
                }
            } else {
                items(products, key = { it.id }) { product ->
                    ProductCardItem(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
