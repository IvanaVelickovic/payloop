package com.app.payloop.data.payment

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object EpcQrBitmapGenerator {

    fun generate(payload: String, sizePx: Int = 720): Result<Bitmap> {
        return runCatching {
            require(payload.isNotBlank()) { "QR payload cannot be empty." }
            require(sizePx > 0) { "QR size must be greater than zero." }

            val matrix = QRCodeWriter().encode(
                payload,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
            )

            val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
            for (x in 0 until sizePx) {
                for (y in 0 until sizePx) {
                    bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        }
    }
}

