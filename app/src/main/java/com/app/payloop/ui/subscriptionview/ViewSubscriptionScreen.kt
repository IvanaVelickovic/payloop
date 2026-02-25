package com.app.payloop.settings

import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.model.normalizeEpochSeconds
import com.app.payloop.data.payment.EpcQrBitmapGenerator
import com.app.payloop.data.payment.EpcQrData
import com.app.payloop.data.payment.EpcQrPayloadBuilder
import com.app.payloop.navigation.NavRoutes
import com.app.payloop.ui.subscriptionview.ViewSubscriptionEvent
import com.app.payloop.ui.subscriptionview.ViewSubscriptionState
import java.io.File
import java.io.FileOutputStream

@Composable
fun ViewSubscriptionScreen(
    state: ViewSubscriptionState,
    subscriptionId: Long,
    navController: NavController,
    onEvent: (ViewSubscriptionEvent) -> Unit
) {
    val subscription = state.subscription
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPaymentQrDialog by remember { mutableStateOf(false) }
    if(subscription == null) {
        Text("Loading")
        return
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            subscriptionName = subscription.name,
            onConfirm = {
                onEvent(ViewSubscriptionEvent.DeleteSubscription)
                showDeleteDialog = false
                navController.popBackStack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    val globarReminder = state.globalReminder
    val reminderEnabled = subscription.isReminderEnabled

    val date = java.util.Date(normalizeEpochSeconds(subscription.nextChargeTimestamp) * 1000)
    val formatter = java.text.SimpleDateFormat("MMM d, yyyy")
    val formattedDate = formatter.format(date)

    var frequency = when(subscription.frequencyUnit) {
        FrequencyUnit.DAY -> "day"
        FrequencyUnit.WEEK -> "week"
        FrequencyUnit.MONTH -> "month"
        FrequencyUnit.YEAR -> "year"
    }

    if (subscription.frequencyInterval > 1) frequency = frequency + "s"
    val frequencyInterval = if (subscription.frequencyInterval == 1) "" else subscription.frequencyInterval.toString() + " "

    val pricePerPerson =String.format("%.2f", subscription.price / 100f / (subscription.sharedWith + 1))
    val sharedWith = if(subscription.sharedWith == 1) "person" else "people"
    val perPersonCents = if (subscription.sharedWith > 0) {
        subscription.price / (subscription.sharedWith + 1)
    } else {
        0L
    }

    if (showPaymentQrDialog) {
        SharedPaymentQrDialog(
            subscriptionName = subscription.name,
            amountInCents = perPersonCents,
            currency = state.currency,
            receiverName = state.receiverName,
            receiverIban = state.receiverIban,
            receiverBic = state.receiverBic,
            receiverPaymentNote = state.receiverPaymentNote,
            onDismiss = { showPaymentQrDialog = false },
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
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
                    text = "Subscription",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Subscription header (logo and name)
            SubscriptionHeader(subscription = subscription)

            Divider(modifier = Modifier.padding(horizontal = 14.dp))

            // Price
            SubscriptionDetailRow(
                icon = "$",
                iconBackgroundColor = Color(0xFFE3EDFF),
                iconTextColor = Color(0xFF5B7FBD),
                label = if(subscription.price != 0L) "Price" else "Frequency",
                value = if(subscription.price != 0L) "${subscription.price/100f}${state.currency} â€¢ Every ${frequencyInterval}${frequency}"
                else "Every ${frequencyInterval}${frequency}",
                useTextIcon = true
            )

            // Next charge
            SubscriptionDetailRow(
                icon = Icons.Outlined.DateRange,
                iconBackgroundColor = Color(0xFFE3EDFF),
                iconTint = Color(0xFF5B7FBD),
                label = if (subscription.isTrial) "Free trial ends on" else "Next charge",
                value = formattedDate
            )

            if(subscription.sharedWith > 0){
                //Shared with
                SubscriptionDetailRow(
                    icon = Icons.Outlined.People,
                    iconBackgroundColor = Color(0xFFE3EDFF),
                    iconTint = Color(0xFF5B7FBD),
                    label = "Shared with ${subscription.sharedWith} $sharedWith",
                    value = "${pricePerPerson}${state.currency} per person"
                )

                SharedPaymentQrAction(
                    onClick = { showPaymentQrDialog = true }
                )
            }

            //Reminder
            ReminderRow(
                enabled = reminderEnabled,
                onEnabledChange = { newValue ->
                    onEvent(ViewSubscriptionEvent.ToggleReminder(newValue))
                },
                reminderDaysBefore = subscription.reminderDaysBefore,
                globalReminder = globarReminder
            )

            if(!globarReminder){
                Row() {
                    Text("Cannot enable while global reminders are off. Go to settings and enable Default Reminders to change this.",
                        modifier = Modifier.padding(start = 22.dp, end = 20.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF4A5565))
                }
            }


            // Action buttons
            ActionButtons(
                onDelete = {
                    showDeleteDialog = true
                },
                onEdit = {
                    navController.navigate(
                        NavRoutes.EditSubscription.createRoute(subscription.id.toLong())
                    )
                }
            )

        }


    }


}

@Composable
fun SharedPaymentQrAction(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .fillMaxWidth(),
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE3EDFF),
                contentColor = Color(0xFF1D4ED8),
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Show Payment QR",
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun SharedPaymentQrDialog(
    subscriptionName: String,
    amountInCents: Long,
    currency: String,
    receiverName: String,
    receiverIban: String,
    receiverBic: String,
    receiverPaymentNote: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var saveFeedback by remember { mutableStateOf<String?>(null) }
    val amountEurText = String.format("%.2f", amountInCents / 100f)
    val isEurCurrency = currency.contains("€") || currency.contains("â‚¬") || currency.equals("EUR", true)
    val payloadInput = remember(
        subscriptionName,
        amountInCents,
        receiverName,
        receiverIban,
        receiverBic,
        receiverPaymentNote,
    ) {
        EpcQrData(
            beneficiaryName = receiverName,
            iban = receiverIban,
            bic = receiverBic.ifBlank { null },
            amountInCents = amountInCents,
            remittanceUnstructured = if (receiverPaymentNote.isBlank()) {
                "$subscriptionName shared subscription"
            } else {
                receiverPaymentNote
            },
        )
    }

    val validationErrors = remember(payloadInput, isEurCurrency) {
        val errors = EpcQrPayloadBuilder.validate(payloadInput).toMutableList()
        if (!isEurCurrency) {
            errors += "EPC QR supports only EUR. Change app currency to EUR for this payment flow."
        }
        errors
    }
    val payloadResult = remember(validationErrors, payloadInput) {
        if (validationErrors.isEmpty()) {
            EpcQrPayloadBuilder.build(payloadInput)
        } else {
            Result.failure(IllegalArgumentException("Validation failed."))
        }
    }
    val qrResult = remember(payloadResult) {
        payloadResult.fold(
            onSuccess = { payload -> EpcQrBitmapGenerator.generate(payload, 720) },
            onFailure = { Result.failure(it) },
        )
    }
    val qrBitmap = qrResult.getOrNull()
    val payloadText = payloadResult.getOrNull().orEmpty()
    val shareText = "Shared subscription payment for $subscriptionName, amount $amountEurText EUR"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Shared Payment QR", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Per-person amount: $amountEurText EUR",
                    color = Color(0xFF4A5565),
                    fontSize = 14.sp,
                )
                if (validationErrors.isEmpty()) {
                    qrBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Shared payment EPC QR code",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .height(220.dp),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Button(
                            onClick = {
                                if (qrBitmap != null) {
                                    val result = saveQrToPictures(
                                        context = context,
                                        bitmap = qrBitmap,
                                        namePrefix = "payloop_shared_qr",
                                    )
                                    saveFeedback = if (result.isSuccess) {
                                        "Saved to Pictures/Payloop."
                                    } else {
                                        "Could not save image."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE5E7EB),
                                contentColor = Color(0xFF1F2937),
                            ),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text("Save image")
                        }
                    }
                } else {
                    validationErrors.forEach { err ->
                        Text(
                            text = "- $err",
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
                saveFeedback?.let { feedback ->
                    Text(
                        text = feedback,
                        color = Color(0xFF166534),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (qrBitmap != null && payloadText.isNotBlank()) {
                        shareQrImage(
                            context = context,
                            bitmap = qrBitmap,
                            shareText = "$shareText\n\n$payloadText",
                        )
                    }
                },
                enabled = qrBitmap != null && validationErrors.isEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B7FBD)),
            ) {
                Text("Share", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
            ) {
                Text("Close", color = Color(0xFF1F2937))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
    )
}

private fun shareQrImage(
    context: android.content.Context,
    bitmap: Bitmap,
    shareText: String,
) {
    val cacheDir = File(context.cacheDir, "shared_qr")
    if (!cacheDir.exists()) cacheDir.mkdirs()
    val imageFile = File(cacheDir, "payment_qr.png")
    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile,
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, shareText)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share payment QR"))
}

private fun saveQrToPictures(
    context: android.content.Context,
    bitmap: Bitmap,
    namePrefix: String,
): Result<Unit> {
    return runCatching {
        val resolver = context.contentResolver
        val fileName = "${namePrefix}_${System.currentTimeMillis()}.png"
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Payloop")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: error("Failed to create MediaStore record.")

        resolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        } ?: error("Failed to open output stream.")
    }
}

@Composable
fun SubscriptionHeader(subscription: Subscription) {
    val iconColor = subscription.color?.let { Color(it) } ?: Color.Gray
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp, end = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = subscription.name.first().toString(),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        iconColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(15.dp)
                    .width(36.dp)
                    .height(36.dp)
            )
        }
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = subscription.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 2.dp, top = 2.dp)
            )
            if(subscription.isTrial) {
                Text(
                    text = "FREE TRIAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFBB4D00),
                    modifier = Modifier
                        .background(
                            Color(0xFFFEF3C6),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 1.dp)
                )
            }
            else if(subscription.isManual){
                Text(
                    text = "PAY YOURSELF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1447E6),
                    modifier = Modifier
                        .background(
                            Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 1.dp)
                )
            }
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
    onEnabledChange: (Boolean) -> Unit,
    reminderDaysBefore : Int,
    globalReminder: Boolean
) {
    val frequency = if(reminderDaysBefore == 1) "day" else "days"
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
                    text = "${reminderDaysBefore} $frequency before",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Switch(
            checked = enabled,
            onCheckedChange = { newValue ->
                onEnabledChange(newValue) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5B7FBD),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.Transparent
            ),
            modifier = Modifier.height(12.dp),
            enabled = globalReminder
        )
    }
}

@Composable
fun ActionButtons(
    onDelete : () -> Unit = {},
    onEdit : () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 26.dp, end = 26.dp, bottom = 8.dp, top = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Delete button
        Button(
            onClick = onDelete,
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
            onClick = onEdit,
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

@Composable
fun DeleteConfirmationDialog(
    subscriptionName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Subscription?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to delete $subscriptionName? This action cannot be undone.")
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




