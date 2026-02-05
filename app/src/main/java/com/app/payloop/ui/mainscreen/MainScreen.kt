package com.app.payloop.ui.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toLong
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.payloop.data.model.Subscription
import com.app.payloop.navigation.NavRoutes
import androidx.compose.foundation.clickable


@Composable
fun MainScreen(
    state: MainScreenState,
    navController: NavController,
    //onEvent: (MainScreenEvent) -> Unit = {}
) {
   // val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* navigate */ },
                containerColor = Color(0xFF5B7FBD),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = "Add new",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Subscription",
                        tint = Color.White
                    )
                }

            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                //.padding(padding)
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp // this bottom padding fixes the shadow cut-off
                )
            ) {
                // Summary card
                item {
                    SummaryCard()
                }

                // Subscription cards
                items(items = state.subscriptions) { subscription ->
                    SubscriptionCard(subscription = subscription,
                        onClick = {
                            navController.navigate(
                                NavRoutes.ViewSubscription.createRoute(subscription.id.toLong())
                            )
                        })
                }

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
fun SubscriptionCard(subscription: Subscription,
                     onClick: () -> Unit) {
    val cardColor = subscription.color?.let { Color(it) } ?: Color.Gray
    val reminderColor = if (subscription.isReminderEnabled) Color(0xFFFE9A00) else Color.LightGray



    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
            .clickable{onClick()}
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
                    text = subscription.name.first().toString(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(
                            cardColor,
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
                    text = subscription.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                //hereeee
                CardText(subscription = subscription)
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
                    tint = reminderColor,
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }
}

@Composable
fun CardText(subscription: Subscription) {
    val date = java.util.Date(subscription.nextChargeTimestamp * 1000)
    val formatter = java.text.SimpleDateFormat("MMM d, yyyy")
    val formattedDate = formatter.format(date)

    val frequency = if(subscription.frequencyInterval == 1) "" else subscription.frequencyInterval.toString() + " "
    val frequencyUnit = if(frequency == "") subscription.frequencyUnit.toString().lowercase() else subscription.frequencyUnit.toString().lowercase() + "s"



    if(subscription.isTrial){
        Text(
            text = buildAnnotatedString {
                append("Trial ends: ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5B7FBD)
                    )
                ) {
                    append(formattedDate)
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 7.dp)
        )
    } else {
        Text(
            text = "Every $frequency$frequencyUnit • ${subscription.price/100f}€",
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
                    append(formattedDate)
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 7.dp)
        )
    }
    CardTag(subscription = subscription)
}

@Composable
fun CardTag(subscription: Subscription){
    val sharedWith = if(subscription.sharedWith == 1) "person" else "people"
    val pricePerPerson =String.format("%.2f", subscription.price / 100f / (subscription.sharedWith + 1))

    if(subscription.isTrial) {
        Text(
            text = "FREE TRIAL • ${subscription.price / 100f}€ after",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFBB4D00),
            modifier = Modifier
                .background(
                    Color(0xFFFEF3C6),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
    else {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if(subscription.isManual){
                Text(
                    text = "PAY YOURSELF",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1447E6),
                    modifier = Modifier
                        .background(
                            Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )

            }
            if(subscription.sharedWith > 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Shared with ${subscription.sharedWith} ${sharedWith}•${pricePerPerson}€/person",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF364153),
                        modifier = Modifier
                            .background(
                                Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

    }

}