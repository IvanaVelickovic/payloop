package com.app.payloop.data.payment

import java.nio.charset.StandardCharsets
import kotlin.math.absoluteValue

data class EpcQrData(
    val beneficiaryName: String,
    val iban: String,
    val bic: String? = null,
    val amountInCents: Long? = null,
    val purpose: String? = null,
    val remittanceStructured: String? = null,
    val remittanceUnstructured: String? = null,
    val beneficiaryToOriginatorInfo: String? = null,
)

object EpcQrPayloadBuilder {
    private const val MAX_PAYLOAD_BYTES = 331
    private const val MIN_AMOUNT_CENTS = 1L
    private const val MAX_AMOUNT_CENTS = 99_999_999_999L

    fun validate(data: EpcQrData): List<String> {
        val errors = mutableListOf<String>()

        val name = data.beneficiaryName.trim()
        val iban = normalizeIban(data.iban)
        val bic = normalizeBic(data.bic.orEmpty())
        val purpose = data.purpose.orEmpty().trim()
        val remStructured = data.remittanceStructured.orEmpty().trim()
        val remUnstructured = data.remittanceUnstructured.orEmpty().trim()
        val btoInfo = data.beneficiaryToOriginatorInfo.orEmpty().trim()

        if (name.isBlank()) errors += "Beneficiary name is required."
        if (name.length > 70) errors += "Beneficiary name cannot exceed 70 characters."

        if (iban.isBlank()) {
            errors += "IBAN is required."
        } else {
            if (iban.length > 34) errors += "IBAN cannot exceed 34 characters."
            if (!isIbanValid(iban)) errors += "IBAN is invalid."
        }

        if (bic.isNotBlank() && !isBicValid(bic)) {
            errors += "BIC must be 8 or 11 alphanumeric characters."
        }

        data.amountInCents?.let { cents ->
            if (cents < MIN_AMOUNT_CENTS || cents > MAX_AMOUNT_CENTS) {
                errors += "Amount must be between 0.01 and 999999999.99 EUR."
            }
        }

        if (purpose.isNotBlank()) {
            if (purpose.length > 4 || !purpose.all { it.isLetterOrDigit() }) {
                errors += "Purpose must be up to 4 alphanumeric characters."
            }
        }

        if (remStructured.isNotBlank() && remUnstructured.isNotBlank()) {
            errors += "Use either structured or unstructured remittance, not both."
        }

        if (remStructured.length > 35) {
            errors += "Structured remittance cannot exceed 35 characters."
        }

        if (remUnstructured.length > 140) {
            errors += "Unstructured remittance cannot exceed 140 characters."
        }

        if (btoInfo.length > 70) {
            errors += "Beneficiary to originator info cannot exceed 70 characters."
        }

        return errors
    }

    fun build(data: EpcQrData): Result<String> {
        val errors = validate(data)
        if (errors.isNotEmpty()) {
            return Result.failure(IllegalArgumentException(errors.joinToString(" ")))
        }

        val amountString = data.amountInCents?.let { cents ->
            "EUR${formatAmount(cents)}"
        }.orEmpty()

        val lines = mutableListOf(
            "BCD",
            "002",
            "1",
            "SCT",
            normalizeBic(data.bic.orEmpty()),
            data.beneficiaryName.trim(),
            normalizeIban(data.iban),
            amountString,
            data.purpose.orEmpty().trim(),
            data.remittanceStructured.orEmpty().trim(),
            data.remittanceUnstructured.orEmpty().trim(),
            data.beneficiaryToOriginatorInfo.orEmpty().trim(),
        )

        while (lines.isNotEmpty() && lines.last().isEmpty()) {
            lines.removeAt(lines.lastIndex)
        }

        val payload = lines.joinToString("\n")
        val payloadSize = payload.toByteArray(StandardCharsets.UTF_8).size
        if (payloadSize > MAX_PAYLOAD_BYTES) {
            return Result.failure(
                IllegalArgumentException("EPC payload exceeds 331 bytes."),
            )
        }

        return Result.success(payload)
    }

    private fun normalizeIban(value: String): String = value.uppercase().replace(" ", "").trim()

    private fun normalizeBic(value: String): String = value.uppercase().replace(" ", "").trim()

    private fun formatAmount(cents: Long): String {
        val euros = cents / 100
        val remainingCents = (cents % 100).absoluteValue
        return "$euros.${remainingCents.toString().padStart(2, '0')}"
    }

    private fun isBicValid(value: String): Boolean {
        if (value.length != 8 && value.length != 11) return false
        return value.all { it.isLetterOrDigit() }
    }

    private fun isIbanValid(iban: String): Boolean {
        if (iban.length !in 15..34) return false
        if (!iban.take(2).all { it.isLetter() }) return false
        if (!iban.drop(2).all { it.isLetterOrDigit() }) return false

        val rearranged = iban.drop(4) + iban.take(4)
        var remainder = 0

        for (ch in rearranged) {
            val numeric = if (ch.isDigit()) {
                ch.toString()
            } else {
                (ch.code - 'A'.code + 10).toString()
            }

            for (digit in numeric) {
                remainder = (remainder * 10 + (digit - '0')) % 97
            }
        }

        return remainder == 1
    }
}

