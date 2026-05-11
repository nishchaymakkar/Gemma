package com.gemma.app.data.mapper

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import java.io.IOException

fun Uri.toDataUrl(contentResolver: ContentResolver): String {
    val mimeType = contentResolver.getType(this) ?: "image/jpeg"
    val bytes = contentResolver.openInputStream(this)?.use { inputStream ->
        inputStream.readBytes()
    } ?: throw IOException("Unable to read image bytes from $this")

    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    return "data:$mimeType;base64,$base64"
}
