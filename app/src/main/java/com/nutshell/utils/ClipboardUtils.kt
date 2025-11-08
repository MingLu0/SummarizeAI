package com.nutshell.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun copyToClipboard(text: String, label: String = "Summary") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
