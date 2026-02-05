package com.app.payloop.ui.add_subscription

import com.app.payloop.data.model.FrequencyUnit

// Ovaj kod mi je na prvu bia cudan jer je interface, a nigdi ne vidis metode. Kao sta?
// Ovo je kao nesto sta odskace od klasicne OOP paradigme. Bilo bi glupo recimo da je umjesto ovoga
// napravljeno da moras implementirat funkciju "EnteredName", "EnteredPrice", ... Ovako samo dobijes
// vrijednost i onda na osnovu nje odredis sta ce se runnat, ko u nekom switch caseu.
// Onda mi nije bilo jasno zasto samo ne koristim ENUM obicni, ali ovo je u biti ENUM na steroidima
// jer mozes poslat i konkretnu vrijednost uz njega (ne samo ENTERED.NAME, nego i koje je ime).
sealed interface AddSubscriptionEvent {

    // Ovo uzimamo da bude object jer zelimo da bude singleton
    object OnNextStep: AddSubscriptionEvent
    object OnPreviousStep: AddSubscriptionEvent

    // 1. Naming
    data class EnteredName(val value: String): AddSubscriptionEvent
    data class EnteredColor(val value: Int?): AddSubscriptionEvent
    data class EnteredIcon(val value: String): AddSubscriptionEvent

    // 2. Frequency
    data class EnteredIsTrial(val value: Boolean): AddSubscriptionEvent
    data class EnteredNextChargeTimeStamp(val value: Long): AddSubscriptionEvent
    data class EnteredFrequencyUnit(val value: FrequencyUnit): AddSubscriptionEvent
    data class EnteredFrequencyInterval(val value: Int): AddSubscriptionEvent
    data class EnteredIsManual(val value: Boolean): AddSubscriptionEvent // za booleane se moze koristit object pa ga samo okrecemo, ali ovo je sigurnije

    // 3. Reminders and pricing
    data class EnteredIsReminderEnabled(val value: Boolean): AddSubscriptionEvent
    data class EnteredPrice(val value: String): AddSubscriptionEvent
    data class EnteredReminderDaysBefore(val value: Int): AddSubscriptionEvent
    data class EnteredSharedWith(val value: Int): AddSubscriptionEvent
}