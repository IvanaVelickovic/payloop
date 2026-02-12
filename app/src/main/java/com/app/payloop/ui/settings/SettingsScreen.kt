package com.app.payloop.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.payloop.settings.DeleteConfirmationDialog
import com.app.payloop.ui.subscriptionview.ViewSubscriptionEvent

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
) {
    val hourlyWage by viewModel.hourlyWage.collectAsState()
    val currencyOptions = listOf("€", "$", "£", "¥")
    val selectedCurrency by viewModel.currency.collectAsState()
    val notificationsEnabled by viewModel.reminderOn.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteAll()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

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
                    .clickable{
                        navController.popBackStack()
                    }
            )
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hourly wage section
            HourlyWageSection(
                value = hourlyWage.toString(),
                onValueChange = { newValue ->
                    newValue.toDoubleOrNull()?.let {
                        viewModel.updateHourlyWage(it)
                    }
                },
                selectedCurrency = selectedCurrency
            )

            // Currency selection section
            CurrencySelectionSection(
                options = currencyOptions,
                selectedOption = selectedCurrency,
                onOptionSelected = { viewModel.updateCurrency(it) }
            )

            // Notifications section
            NotificationsSection(
                enabled = notificationsEnabled,
                onEnabledChange = { viewModel.updateReminder(it) }
            )

            // Save button
            //SaveButton()

            Divider()

            // Danger zone section
            DangerZoneSection(onClick = {
                showDeleteDialog = true
            })
        }
    }
}

@Composable
fun HourlyWageSection(
    value: String,
    onValueChange: (String) -> Unit,
    selectedCurrency : String
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF5B7FBD))) {
                        append("$  ")
                    }
                    append("Hourly Wage")
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Set your hourly wage to calculate how many hours you need to work to cover subscription costs.",
                color = Color(0xFF4A5565),
                modifier = Modifier.padding(top = 16.dp, bottom = 14.dp),
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                label = {
                    Text(
                        "Hourly rate",
                        fontSize = 15.sp,
                        modifier = Modifier.background(Color.Transparent)
                    )
                },
                suffix = {
                    Text(
                        "${selectedCurrency}/h",
                        color = Color.Gray,
                        fontSize = 18.sp,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B7FBD),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun CurrencySelectionSection(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(modifier = Modifier.padding(18.dp)) {
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF5B7FBD))) {
                        append("€  ")
                    }
                    append("Currency")
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    FilterChip(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) },
                        label = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(option, fontSize = 18.sp)
                            }
                        },
                        modifier = Modifier
                            .weight(0.8f)
                            .height(50.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFF6F7FA),
                            selectedLabelColor = Color(0xFF5B7FBD)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedOption == option,
                            borderColor = Color(0xFFD1D5DC),
                            borderWidth = 1.dp,
                            selectedBorderColor = Color(0xFF5B7FBD),
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationsSection(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 36.dp)) {
        Column {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF5B7FBD),
                    modifier = Modifier
                        .height(22.dp)
                        .padding(end = 6.dp)
                )
                Text(
                    text = "Default Reminder",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .height(50.dp)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Enable notifications",
                    fontSize = 17.sp
                )

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

            Row() {
                Text("Disabling this will turn off all notifications across the app.",
                    color = Color(0xFF4A5565),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp
                    ),
                    fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SaveButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 34.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B7FBD),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Save changes",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun DangerZoneSection(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFC10007),
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "Danger zone",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFC10007)
                )
            }
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 20.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFFEF2F2),
                    contentColor = Color(0xFFC10007),
                ),
                border = BorderStroke(1.dp, Color(0xFFC10007)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Delete All Subscriptions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "This action cannot be undone. All your subscription data will be permanently deleted.",
                color = Color(0xFF4A5565),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete All Subscriptions?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to delete all subscriptions? This action is permanent and cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC10007)
                )
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF3F4F6),
                    contentColor = Color(0xFF4A5565)
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}