package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BgBottomNav
import com.example.ui.theme.BgHeader
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.Color0B0F1A
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppTab

@Composable
fun HeaderSection(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgHeader)
            .border(width = 0.dp, color = Color.Transparent)
            .padding(top = 18.dp, bottom = 14.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AccentCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Maamulaha Bakhaarka",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Shelf Controller · Stock In/Out",
            fontSize = 12.5.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TabChip(
                label = "📦 Stock",
                isActive = selectedTab == AppTab.STOCK,
                onClick = { onTabSelected(AppTab.STOCK) }
            )
            TabChip(
                label = "📋 Alaabta oo dhan",
                isActive = selectedTab == AppTab.PRODUCTS,
                onClick = { onTabSelected(AppTab.PRODUCTS) }
            )
            TabChip(
                label = "📊 Reports",
                isActive = selectedTab == AppTab.REPORTS,
                onClick = { onTabSelected(AppTab.REPORTS) }
            )
        }
    }
}

@Composable
private fun TabChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val chipShape = RoundedCornerShape(20.dp)
    val modifier = if (isActive) {
        Modifier
            .clip(chipShape)
            .background(AccentGradient)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    } else {
        Modifier
            .clip(chipShape)
            .background(CardDark)
            .border(1.dp, BorderDark, chipShape)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    }

    Box(
        modifier = modifier.clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isActive) Color0B0F1A else TextMuted
        )
    }
}

@Composable
fun BottomNavSection(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onRegisterClick: () -> Unit,
    onStockOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgBottomNav)
            .border(width = 1.dp, color = BorderDark, shape = RoundedCornerShape(0.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stock
            NavButtonItem(
                icon = "📦",
                label = "Stock",
                isActive = selectedTab == AppTab.STOCK,
                onClick = { onTabSelected(AppTab.STOCK) },
                modifier = Modifier.weight(1f)
            )

            // Liiska
            NavButtonItem(
                icon = "📋",
                label = "Liiska",
                isActive = selectedTab == AppTab.PRODUCTS,
                onClick = { onTabSelected(AppTab.PRODUCTS) },
                modifier = Modifier.weight(1f)
            )

            // Diiwaan geli (Register Button)
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentGradient)
                    .clickable { onRegisterClick() }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "➕", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Diiwaan geli",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color0B0F1A
                    )
                }
            }

            // Stock Out
            NavButtonItem(
                icon = "📤",
                label = "Stock Out",
                isActive = false,
                onClick = { onStockOutClick() },
                modifier = Modifier.weight(1f)
            )

            // Reports
            NavButtonItem(
                icon = "📊",
                label = "Reports",
                isActive = selectedTab == AppTab.REPORTS,
                onClick = { onTabSelected(AppTab.REPORTS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavButtonItem(
    icon: String,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 19.sp)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isActive) AccentCyan else TextMuted
            )
        }
    }
}
