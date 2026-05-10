package com.gemma.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {

    fun compressImage(context: Context, uri: Uri): Uri {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Calculate inSampleSize
            var inSampleSize = 1
            val maxDim = 1920
            if (options.outHeight > maxDim || options.outWidth > maxDim) {
                val halfHeight: Int = options.outHeight / 2
                val halfWidth: Int = options.outWidth / 2
                while (halfHeight / inSampleSize >= maxDim && halfWidth / inSampleSize >= maxDim) {
                    inSampleSize *= 2
                }
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            val inputStream2 = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, options)
            inputStream2?.close()

            bitmap?.let {
                val file = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
                var quality = 90
                var streamLength: Long
                do {
                    val bmpStream = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.JPEG, quality, bmpStream)
                    bmpStream.close()
                    streamLength = file.length()
                    quality -= 10
                } while (streamLength > 1024 * 1024 && quality > 10) // target < 1MB

                Uri.fromFile(file)
            } ?: uri
        } catch (e: Exception) {
            e.printStackTrace()
            uri
        }
    }
}
