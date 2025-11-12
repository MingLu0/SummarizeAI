package com.nutshell.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePickerUtils @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    @Composable
    fun rememberFilePicker(
        onFileSelected: (Uri) -> Unit,
    ): () -> Unit {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { onFileSelected(it) }
        }

        return {
            launcher.launch("*/*") // Accept all file types
        }
    }

    fun showUnsupportedFileToast() {
        Toast.makeText(
            context,
            "Unsupported file type. Please select a PDF or DOC file.",
            Toast.LENGTH_LONG,
        ).show()
    }

    fun showFileReadErrorToast() {
        Toast.makeText(
            context,
            "Error reading file. Please try again.",
            Toast.LENGTH_LONG,
        ).show()
    }
}
