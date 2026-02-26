package com.app.payloop.ui.add_subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.payloop.ui.theme.InactiveTrack
import com.app.payloop.ui.theme.PayLoopBackground
import com.app.payloop.ui.theme.PayLoopBlue
import com.app.payloop.ui.theme.PayLoopDarkBlue
import com.app.payloop.ui.theme.PayLoopSurface
import com.app.payloop.ui.theme.TextPrimary
import com.app.payloop.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val inputShape = RoundedCornerShape(14.dp)
private val cardShape = RoundedCornerShape(16.dp)
private val emojiOptions = listOf("💰", "📺", "🎵", "☁️", "🎮", "📱", "💪", "🍕", "🚗", "📚", "✈️", "🏠")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionFlowScreen(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var showNextChargeDatePicker by remember { mutableStateOf(false) }
    var showTrialEndDatePicker by remember { mutableStateOf(false) }
    val nextChargeDatePickerState = rememberDatePickerState()
    val trialEndDatePickerState = rememberDatePickerState()

    Scaffold(
        containerColor = PayLoopBackground,
        topBar = {
            AddSubscriptionTopBar(
                onBackClick = {
                    if (state.currentStep > 1) {
                        onEvent(AddSubscriptionEvent.OnPreviousStep)
                    } else {
                        onNavigateBack()
                    }
                },
            )
        },
        bottomBar = {
            AddSubscriptionBottomBar(
                isLastStep = state.currentStep == state.totalSteps,
                isSaving = state.isSaving,
                onNextClick = {
                    if (state.currentStep == state.totalSteps) {
                        onSubmit()
                    } else {
                        onEvent(AddSubscriptionEvent.OnNextStep)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ProgressBar(currentStep = state.currentStep, totalSteps = state.totalSteps)
            state.saveError?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFB91C1C),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            StepContent(
                state = state,
                onEvent = onEvent,
                onNextChargeDateClick = { showNextChargeDatePicker = true },
                onTrialEndDateClick = { showTrialEndDatePicker = true },
            )
        }
    }

    if (showNextChargeDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showNextChargeDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        nextChargeDatePickerState.selectedDateMillis?.let { millis ->
                            onEvent(AddSubscriptionEvent.NextChargeChanged(millisToIsoDate(millis)))
                        }
                        showNextChargeDatePicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNextChargeDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = nextChargeDatePickerState)
        }
    }

    if (showTrialEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showTrialEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        trialEndDatePickerState.selectedDateMillis?.let { millis ->
                            onEvent(AddSubscriptionEvent.TrialEndDateChanged(millisToIsoDate(millis)))
                        }
                        showTrialEndDatePicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTrialEndDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = trialEndDatePickerState)
        }
    }
}

@Composable
private fun AddSubscriptionTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.horizontalGradient(listOf(PayLoopBlue, PayLoopDarkBlue)))
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }
        Text(
            text = "Add Subscription",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun AddSubscriptionBottomBar(
    isLastStep: Boolean,
    isSaving: Boolean,
    onNextClick: () -> Unit,
) {
    Surface(shadowElevation = 8.dp, color = PayLoopSurface) {
        Button(
            onClick = onNextClick,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PayLoopDarkBlue),
        ) {
            if (isLastStep) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaving) "Saving..." else "Add Subscription",
                    fontWeight = FontWeight.SemiBold,
                )
            } else {
                Text(
                    text = if (isSaving) "Saving..." else "Continue",
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index < currentStep) PayLoopDarkBlue else Color(0xFFE5E7EB),
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun StepContent(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
    onNextChargeDateClick: () -> Unit,
    onTrialEndDateClick: () -> Unit,
) {
    when {
        state.currentStep == 1 -> Step1NameLogoTrial(state, onEvent)
        state.isTrial && state.currentStep == 2 -> Step2TrialDetails(
            state = state,
            onEvent = onEvent,
            onTrialEndDateClick = onTrialEndDateClick,
        )
        state.isTrial && state.currentStep == 3 -> Step3TrialConfirmation(state)
        !state.isTrial && state.currentStep == 2 -> Step2Frequency(
            state = state,
            onEvent = onEvent,
            onNextChargeDateClick = onNextChargeDateClick,
        )
        !state.isTrial && state.currentStep == 3 -> Step3ReminderPrice(state, onEvent)
        else -> Step4Confirmation(state)
    }
}

@Composable
private fun Step1NameLogoTrial(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FieldLabel("Subscription Name")
        FilledInput(
            value = state.name,
            onValueChange = { onEvent(AddSubscriptionEvent.NameChanged(it)) },
            placeholder = "e.g., Netflix, Spotify",
        )

        FieldLabel("Choose an emoji")
        emojiOptions.chunked(6).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { emoji ->
                    val selected = state.emoji == emoji
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .background(
                                color = if (selected) Color(0x1A5B7FBD) else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(10.dp),
                            )
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) PayLoopDarkBlue else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(10.dp),
                            )
                            .clickable { onEvent(AddSubscriptionEvent.EmojiChanged(emoji)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        ToggleCard(
            label = "Is this a trial subscription?",
            checked = state.isTrial,
            onCheckedChange = { onEvent(AddSubscriptionEvent.TrialChanged(it)) },
        )
    }
}

@Composable
private fun Step2TrialDetails(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
    onTrialEndDateClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FieldLabel("Trial End Date")
        DateFieldButton(
            value = state.trialEndDate,
            placeholder = "Pick a date",
            onClick = onTrialEndDateClick,
        )

        ToggleCard(
            label = "Reminder",
            checked = state.trialReminderEnabled,
            onCheckedChange = { onEvent(AddSubscriptionEvent.TrialReminderEnabledChanged(it)) },
        )

        if (state.trialReminderEnabled) {
            FieldLabel("How many days ahead would you like to be reminded?")
            FilledInput(
                value = state.trialReminderDays,
                onValueChange = { onEvent(AddSubscriptionEvent.TrialReminderDaysChanged(it)) },
                placeholder = "3",
                keyboardType = KeyboardType.Number,
            )
        }

        FieldLabel("Price After Trial (optional)")
        MoneyInput(
            value = state.priceAfterTrial,
            onValueChange = { onEvent(AddSubscriptionEvent.PriceAfterTrialChanged(it)) },
            placeholder = "0.00",
        )

        SharedSection(state = state, onEvent = onEvent)
    }
}

@Composable
private fun Step2Frequency(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
    onNextChargeDateClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FieldLabel("How often are you charged?")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            FrequencyButton(
                text = "Daily",
                selected = state.frequency == BillingFrequency.DAILY,
                onClick = { onEvent(AddSubscriptionEvent.FrequencyChanged(BillingFrequency.DAILY)) },
                modifier = Modifier.weight(1f),
            )
            FrequencyButton(
                text = "Weekly",
                selected = state.frequency == BillingFrequency.WEEKLY,
                onClick = { onEvent(AddSubscriptionEvent.FrequencyChanged(BillingFrequency.WEEKLY)) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            FrequencyButton(
                text = "Monthly",
                selected = state.frequency == BillingFrequency.MONTHLY,
                onClick = { onEvent(AddSubscriptionEvent.FrequencyChanged(BillingFrequency.MONTHLY)) },
                modifier = Modifier.weight(1f),
            )
            FrequencyButton(
                text = "Yearly",
                selected = state.frequency == BillingFrequency.YEARLY,
                onClick = { onEvent(AddSubscriptionEvent.FrequencyChanged(BillingFrequency.YEARLY)) },
                modifier = Modifier.weight(1f),
            )
        }

        FieldLabel("Frequency interval")
        FilledInput(
            value = state.frequencyInterval,
            onValueChange = { onEvent(AddSubscriptionEvent.FrequencyIntervalChanged(it)) },
            placeholder = "1",
            keyboardType = KeyboardType.Number,
        )

        FieldLabel("When is your next charge?")
        DateFieldButton(
            value = state.nextCharge,
            placeholder = "Pick a date",
            onClick = onNextChargeDateClick,
        )
    }
}

@Composable
private fun Step3ReminderPrice(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        ToggleCard(
            label = "Reminder",
            checked = state.reminderEnabled,
            onCheckedChange = { onEvent(AddSubscriptionEvent.ReminderEnabledChanged(it)) },
        )

        if (state.reminderEnabled) {
            FieldLabel("How many days ahead would you like to be reminded?")
            FilledInput(
                value = state.reminderDays,
                onValueChange = { onEvent(AddSubscriptionEvent.ReminderDaysChanged(it)) },
                placeholder = "3",
                keyboardType = KeyboardType.Number,
            )
        }

        FieldLabel("Price (optional)")
        MoneyInput(
            value = state.price,
            onValueChange = { onEvent(AddSubscriptionEvent.PriceChanged(it)) },
            placeholder = "0.00",
        )

        SharedSection(state = state, onEvent = onEvent)
    }
}

@Composable
private fun Step3TrialConfirmation(state: SubscriptionUiState) {
    ConfirmationCard(
        title = "Trial Subscription Ready",
        name = state.name.ifBlank { "Unnamed Subscription" },
        badge = "FREE TRIAL",
        lines = buildList {
            add("Trial End Date" to formatDate(state.trialEndDate))
            if (state.trialReminderEnabled) add("Reminder" to "${state.trialReminderDays.ifBlank { "3" }} days before")
            if (state.priceAfterTrial.isNotBlank()) add("Price After Trial" to "${state.priceAfterTrial}€")
            if (state.isSharedSubscription) add("Shared with" to "${state.sharedWith.ifBlank { "1" }} people")
        },
    )
}

@Composable
private fun Step4Confirmation(state: SubscriptionUiState) {
    ConfirmationCard(
        title = "Subscription Ready",
        name = state.name.ifBlank { "Unnamed Subscription" },
        lines = buildList {
            add("Next charge" to formatDate(state.nextCharge))
            add("Frequency" to formatFrequency(state))
                        if (state.reminderEnabled) add("Reminder" to "${state.reminderDays.ifBlank { "3" }} days before")
            if (state.price.isNotBlank()) add("Price" to "${state.price}€")
            if (state.isSharedSubscription) add("Shared with" to "${state.sharedWith.ifBlank { "1" }} people")
        },
    )
}

@Composable
private fun SharedSection(
    state: SubscriptionUiState,
    onEvent: (AddSubscriptionEvent) -> Unit,
) {
    ToggleCard(
        label = "Is this subscription shared with anyone?",
        checked = state.isSharedSubscription,
        onCheckedChange = { onEvent(AddSubscriptionEvent.SharedSubscriptionChanged(it)) },
    )

    if (state.isSharedSubscription) {
        FieldLabel("How many other people is it shared with?")
        FilledInput(
            value = state.sharedWith,
            onValueChange = { onEvent(AddSubscriptionEvent.SharedWithChanged(it)) },
            placeholder = "1",
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun ConfirmationCard(
    title: String,
    name: String,
    lines: List<Pair<String, String>>,
    badge: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFD1FAE5), CircleShape)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF059669),
                modifier = Modifier.size(36.dp),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Card(
            shape = cardShape,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF9FAFB))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    if (!badge.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = badge,
                            color = Color(0xFF1D4ED8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(Color(0xFFDBEAFE), RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                lines.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = label, color = TextSecondary, fontSize = 14.sp)
                        Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun FilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextSecondary, fontSize = 14.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = inputShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF3F3F5),
            unfocusedContainerColor = Color(0xFFF3F3F5),
            disabledContainerColor = Color(0xFFF3F3F5),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DateFieldButton(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF3F3F5),
            contentColor = TextPrimary,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = if (value.isBlank()) placeholder else formatDate(value),
            color = if (value.isBlank()) TextSecondary else TextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun MoneyInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        FilledInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            keyboardType = KeyboardType.Decimal,
        )
        Text(
            text = "€",
            color = TextSecondary,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
        )
    }
}

@Composable
private fun ToggleCard(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F5), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                checkedTrackColor = PayLoopDarkBlue,
                uncheckedTrackColor = InactiveTrack,
            ),
        )
    }
}

@Composable
private fun FrequencyButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0x1A5B7FBD) else Color.White,
            contentColor = TextPrimary,
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = if (selected) PayLoopDarkBlue else Color(0xFFE5E7EB),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.Normal, fontSize = 14.sp)
    }
}

private fun formatDate(dateString: String): String {
    if (dateString.isBlank()) return "Not set"
    return try {
        val parsed = LocalDate.parse(dateString)
        parsed.format(DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.US))
    } catch (_: DateTimeParseException) {
        dateString
    }
}

private fun formatFrequency(state: SubscriptionUiState): String {
    return when (state.frequency) {
        BillingFrequency.DAILY -> "Every ${state.frequencyInterval.ifBlank { "?" }} day(s)"
        BillingFrequency.WEEKLY -> "Every ${state.frequencyInterval.ifBlank { "?" }} week(s)"
        BillingFrequency.MONTHLY -> "Every ${state.frequencyInterval.ifBlank { "?" }} month(s)"
        BillingFrequency.YEARLY -> "Every ${state.frequencyInterval.ifBlank { "?" }} year(s)"
    }
}

private fun millisToIsoDate(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}


