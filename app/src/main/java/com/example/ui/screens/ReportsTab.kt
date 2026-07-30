package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.LogEntry
import com.example.ui.components.AccentGradient
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentCyanAlpha15
import com.example.ui.theme.BorderDark
import com.example.ui.theme.Card2Dark
import com.example.ui.theme.Color0B0F1A
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedAlpha15
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.ReportFilter
import com.example.util.DateUtils
import kotlinx.coroutines.delay

@Composable
fun ReportsTabScreen(
    currentFilter: ReportFilter,
    onFilterChange: (ReportFilter) -> Unit,
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Live update every 2 seconds for relative timeAgo indicators
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            nowMs = System.currentTimeMillis()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        // Filter Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            FilterButton(
                label = "Dhammaan",
                isActive = currentFilter == ReportFilter.ALL,
                activeBgGradient = true,
                onClick = { onFilterChange(ReportFilter.ALL) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterButton(
                label = "⬇️ Stock In",
                isActive = currentFilter == ReportFilter.IN,
                activeColor = AccentCyan,
                activeBgColor = AccentCyanAlpha15,
                onClick = { onFilterChange(ReportFilter.IN) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterButton(
                label = "⬆️ Stock Out",
                isActive = currentFilter == ReportFilter.OUT,
                activeColor = DangerRed,
                activeBgColor = DangerRedAlpha15,
                onClick = { onFilterChange(ReportFilter.OUT) },
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (logs.isEmpty()) {
                item {
                    EmptyStateView(message = "Weli dhaqdhaqaaq lama diiwaan gelin.")
                }
            } else {
                items(logs, key = { it.id }) { log ->
                    ReportItemRow(
                        log = log,
                        currentTimeMs = nowMs
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FilterButton(
    label: String,
    isActive: Boolean,
    activeBgGradient: Boolean = false,
    activeColor: androidx.compose.ui.graphics.Color = AccentCyan,
    activeBgColor: androidx.compose.ui.graphics.Color = AccentCyanAlpha15,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val btnShape = RoundedCornerShape(12.dp)
    val btnModifier = if (isActive) {
        if (activeBgGradient) {
            Modifier
                .clip(btnShape)
                .background(AccentGradient)
        } else {
            Modifier
                .clip(btnShape)
                .background(activeBgColor)
                .border(1.dp, activeColor, btnShape)
        }
    } else {
        Modifier
            .clip(btnShape)
            .background(Card2Dark)
            .border(1.dp, BorderDark, btnShape)
    }

    Box(
        modifier = modifier
            .then(btnModifier)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) {
                if (activeBgGradient) Color0B0F1A else activeColor
            } else TextMuted
        )
    }
}

@Composable
private fun ReportItemRow(
    log: LogEntry,
    currentTimeMs: Long,
    modifier: Modifier = Modifier
) {
    val isIn = log.type == "in"
    val iconBg = if (isIn) AccentCyanAlpha15 else DangerRedAlpha15
    val iconText = if (isIn) "⬇️" else "⬆️"
    val typeTitle = if (isIn) "Stock In" else "Stock Out"
    val notePart = if (log.note.isNotBlank()) " · ${log.note}" else ""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Card2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconText, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (log.productName.isNotBlank()) log.productName else "Alaab la tirtiray",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$typeTitle · ${log.amount} halkii$notePart",
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Time ago
            Text(
                text = DateUtils.timeAgo(log.timestamp, currentTimeMs),
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.End
            )
        }
    }
}
