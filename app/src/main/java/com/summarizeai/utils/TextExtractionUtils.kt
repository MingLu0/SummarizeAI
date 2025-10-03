package com.summarizeai.utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextExtractionUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun extractTextFromUri(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Could not open file"))
            
            val fileName = getFileName(uri)
            val fileExtension = fileName.substringAfterLast('.', "").lowercase()
            
            val text = when (fileExtension) {
                "pdf" -> extractTextFromPdf(inputStream)
                "doc", "docx" -> extractTextFromDoc(inputStream)
                else -> throw UnsupportedOperationException("Unsupported file type: $fileExtension")
            }
            
            inputStream.close()
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun extractTextFromPdf(inputStream: InputStream): String {
        return try {
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            text
        } catch (e: Exception) {
            throw Exception("Error reading PDF: ${e.message}")
        }
    }
    
    private fun extractTextFromDoc(inputStream: InputStream): String {
        // For DOC/DOCX files, we'll use a simple text extraction
        // In a real app, you might want to use Apache POI or similar library
        return try {
            // This is a simplified implementation
            // For production, consider using Apache POI for better DOC/DOCX support
            val bytes = inputStream.readBytes()
            val text = String(bytes, Charsets.UTF_8)
            
            // Clean up the text (remove binary content)
            text.filter { char ->
                char.isLetterOrDigit() || char.isWhitespace() || 
                char in listOf('.', ',', '!', '?', ':', ';', '-', '(', ')', '[', ']', '"', '\'')
            }.trim()
        } catch (e: Exception) {
            throw Exception("Error reading DOC file: ${e.message}")
        }
    }
    
    private fun getFileName(uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex) ?: "unknown_file"
        } ?: "unknown_file"
    }
}
