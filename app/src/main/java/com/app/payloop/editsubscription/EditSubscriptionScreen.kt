package com.app.payloop.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen() {
    // State variables
    var subscriptionName by remember { mutableStateOf("Netflix") }
    var price by remember { mutableStateOf("12") }
    var peopleCount by remember { mutableStateOf("") }
    val frequencyOptions = listOf("Daily", "Monthly", "Yearly")
    var selectedFrequency by remember { mutableStateOf(frequencyOptions[0]) }
    var customFrequencyEnabled by remember { mutableStateOf(false) }
    var customFrequencyValue by remember { mutableStateOf("3") }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderDays by remember { mutableStateOf("3") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf("No date selected") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Status bar spacer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5B7FBD))
                .statusBarsPadding()
        ) {}

        // Header with back button and title
        EditSubscriptionHeader()

        // Form sections
        SubscriptionNameField(
            value = subscriptionName,
            onValueChange = { subscriptionName = it }
        )

        PriceField(
            value = price,
            onValueChange = { price = it }
        )

        FrequencySection(
            options = frequencyOptions,
            selectedOption = selectedFrequency,
            onOptionSelected = { selectedFrequency = it },
            customEnabled = customFrequencyEnabled,
            onCustomToggle = { customFrequencyEnabled = it },
            customValue = customFrequencyValue,
            onCustomValueChange = { customFrequencyValue = it }
        )

        NextChargeDateField(
            selectedDate = selectedDate,
            onDateClick = { showDatePicker = true }
        )

        SharedWithField(
            value = peopleCount,
            onValueChange = { peopleCount = it }
        )

        ReminderToggle(
            enabled = reminderEnabled,
            onToggle = { reminderEnabled = it }
        )

        if (reminderEnabled) {
            ReminderDaysField(
                value = reminderDays,
                onValueChange = { reminderDays = it }
            )
        }

        SaveEditButton()
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        selectedDate = java.text.SimpleDateFormat(
                            "dd/MM/yyyy",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun EditSubscriptionHeader() {
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
            text = "Edit Subscription",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun SubscriptionNameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Subscription name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 17.sp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 26.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B7FBD),
                    unfocusedBorderColor = Color(0xFFD1D5DC),
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(10.dp),
            )
        }
    }
}

@Composable
fun PriceField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Price",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 17.sp),
                suffix = {
                    Text(
                        "€",
                        color = Color.Gray,
                        fontSize = 18.sp,
                    )
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 46.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B7FBD),
                    unfocusedBorderColor = Color(0xFFD1D5DC),
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(10.dp),
            )
        }
    }
}

@Composable
fun FrequencySection(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    customEnabled: Boolean,
    onCustomToggle: (Boolean) -> Unit,
    customValue: String,
    onCustomValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Frequency",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            // Frequency chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                                Text(option, fontSize = 14.sp)
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

            // Custom frequency toggle
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(top = 2.dp, bottom = 2.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom",
                    fontSize = 14.sp
                )
                Switch(
                    checked = customEnabled,
                    onCheckedChange = onCustomToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF5B7FBD),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray,
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(scaleX = 0.8f, scaleY = 0.7f)
                )
            }

            // Custom frequency input
            if (customEnabled) {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                    Text(
                        text = "Enter custom frequency",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = customValue,
                        onValueChange = onCustomValueChange,
                        suffix = {
                            val unit = when (selectedOption) {
                                "Daily" -> "days"
                                "Monthly" -> "months"
                                else -> "years"
                            }
                            Text(
                                unit,
                                color = Color.Gray,
                                fontSize = 18.sp,
                            )
                        },
                        textStyle = TextStyle(fontSize = 17.sp),
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 46.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5B7FBD),
                            unfocusedBorderColor = Color(0xFFD1D5DC),
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedContainerColor = Color(0xFFF9FAFB)
                        ),
                        shape = RoundedCornerShape(10.dp),
                    )
                    Text(
                        text = "Please select Daily for input of days, Monthly for input of months and Yearly for input of years.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NextChargeDateField(
    selectedDate: String,
    onDateClick: () -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Next charge date",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedButton(
                onClick = onDateClick,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF9FAFB)
                ),
                border = BorderStroke(1.dp, Color(0xFFD1D5DC)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = selectedDate,
                    color = if (selectedDate == "No date selected") Color.Gray else Color.Black,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
fun SharedWithField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Shared with (number of people)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 17.sp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 40.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B7FBD),
                    unfocusedBorderColor = Color(0xFFD1D5DC),
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(10.dp),
            )
        }
    }
}

@Composable
fun ReminderToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .background(
                    Color(0xFFF9FAFB),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(top = 2.dp, bottom = 2.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminder",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF5B7FBD),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedBorderColor = Color.Transparent
                ),
                modifier = Modifier.scale(scaleX = 0.85f, scaleY = 0.75f)
            )
        }
    }
}

@Composable
fun ReminderDaysField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                text = "Remind me how many days before?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 17.sp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 46.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B7FBD),
                    unfocusedBorderColor = Color(0xFFD1D5DC),
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(10.dp),
            )
            Text(
                text = "You'll be notified $value days before the next charge",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun SaveEditButton() {
    Button(
        onClick = {},
        modifier = Modifier
            .padding(top = 12.dp, end = 20.dp, start = 20.dp, bottom = 20.dp)
            .height(50.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5B7FBD),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            "Save changes",
            fontSize = 17.sp
        )
    }
}