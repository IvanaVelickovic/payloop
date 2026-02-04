package com.app.payloop.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubscriptionViewScreen() {
    var reminderEnabled by remember { mutableStateOf(false) }

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

        // Header with back button and title
        Row(
            modifier = Modifier
                .background(Color(0xFF5B7FBD))
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .height(38.dp)
                    .width(38.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Subscription",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Subscription header (logo and name)
        SubscriptionHeader()

        Divider(modifier = Modifier.padding(horizontal = 14.dp))

        // Price
        SubscriptionDetailRow(
            icon = "$",
            iconBackgroundColor = Color(0xFFE3EDFF),
            iconTextColor = Color(0xFF5B7FBD),
            label = "Price",
            value = "20€ • Every year",
            useTextIcon = true
        )

        // Next charge
        SubscriptionDetailRow(
            icon = Icons.Outlined.DateRange,
            iconBackgroundColor = Color(0xFFE3EDFF),
            iconTint = Color(0xFF5B7FBD),
            label = "Next charge",
            value = "Feb 17, 2026"
        )

        //Reminder
        ReminderRow(
            enabled = reminderEnabled,
            onEnabledChange = { reminderEnabled = it }
        )

        // Action buttons at bottom
        ActionButtons()
    }
}

@Composable
fun SubscriptionHeader() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp, end = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "N",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        Color(0xFFE7000B),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(15.dp)
                    .width(36.dp)
                    .height(36.dp)
            )
        }
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Netflix",
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp, top = 4.dp)
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
    }
}

@Composable
fun SubscriptionDetailRow(
    icon: Any, // Can be ImageVector or String
    iconBackgroundColor: Color,
    iconTint: Color? = null,
    iconTextColor: Color? = null,
    label: String,
    value: String,
    useTextIcon: Boolean = false
) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .background(
                Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = iconBackgroundColor,
                        shape = RoundedCornerShape(percent = 50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (useTextIcon) {
                    Text(
                        text = icon as String,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = iconTextColor ?: Color.Black,
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon as ImageVector,
                        contentDescription = label,
                        tint = iconTint ?: Color.Black
                    )
                }
            }
        }
        Column(modifier = Modifier.padding(start = 6.dp)) {
            Text(
                text = label,
                color = Color(0xFF4A5565)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ReminderRow(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .background(
                Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = Color(0xFFE3EDFF),
                            shape = RoundedCornerShape(percent = 50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF5B7FBD)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 6.dp)) {
                Text(
                    text = "Reminder",
                    color = Color(0xFF4A5565)
                )
                Text(
                    text = if (enabled) "Enabled" else "Disabled",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5B7FBD),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.Transparent
            ),
            modifier = Modifier.height(12.dp)
        )
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 26.dp, end = 26.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Delete button
        Button(
            onClick = {},
            modifier = Modifier
                .weight(0.9f)
                .padding(top = 16.dp, bottom = 16.dp, end = 8.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFEF2F2),
                contentColor = Color(0xFFC10007),
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFC10007),
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                "Delete",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Edit button
        Button(
            onClick = {},
            modifier = Modifier
                .weight(0.9f)
                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B7FBD),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                "Edit",
                fontSize = 17.sp
            )
        }
    }
}