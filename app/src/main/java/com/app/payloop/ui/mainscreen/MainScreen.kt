package com.app.payloop.ui.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Status bar spacer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5B7FBD))
                .statusBarsPadding()
        ) {}

        // Header with title and settings icon
        Row(
            modifier = Modifier
                .background(Color(0xFF5B7FBD))
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Subscriptions",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
            )
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                SummaryCard()
            }

            // Subscription cards
            items(5) { index ->
                SubscriptionCard()
            }
        }
    }
}

@Composable
fun SummaryCard() {
    Column(
        modifier = Modifier
            .background(
                color = Color(0xFF5B7FBD),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total subscriptions",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "3",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column {
                Text(
                    text = "Monthly cost",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "28.67€",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

        Divider(color = Color.Gray, thickness = 1.5.dp)

        Row(modifier = Modifier.padding(top = 10.dp)) {
            Text(
                text = buildAnnotatedString {
                    append("You need to work ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("1.1 hours/month ")
                    }
                    append("to cover these costs")
                },
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SubscriptionCard() {
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "N",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(
                            Color(0xFFE7000B),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                        .width(22.dp)
                        .height(22.dp)
                )
            }

            // Subscription details
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .padding(vertical = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Netflix",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "Every month • 15€",
                    color = Color(0xFF5B7FBD),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append("Next charge date: ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5B7FBD)
                            )
                        ) {
                            append("Feb 7, 2026")
                        }
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 7.dp)
                )
                Text(
                    text = "PAY YOURSELF",
                    fontSize = 12.sp,
                    color = Color(0xFF1447E6),
                    modifier = Modifier
                        .background(
                            Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Notification icon
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp, top = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFFFE9A00),
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }
}