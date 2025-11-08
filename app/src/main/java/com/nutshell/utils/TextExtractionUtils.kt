package com.nutshell.utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextExtractionUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private var isPdfBoxInitialized = false
        private var pdfBoxInitializationFailed = false
        
        init {
            // Try to initialize PDFBox classes early to catch initialization errors
            try {
                Class.forName("com.tom_roush.pdfbox.text.LegacyPDFStreamEngine")
                isPdfBoxInitialized = true
            } catch (e: ExceptionInInitializerError) {
                android.util.Log.w("TextExtractionUtils", "PDFBox static initialization failed", e)
                pdfBoxInitializationFailed = true
            } catch (e: Exception) {
                android.util.Log.w("TextExtractionUtils", "PDFBox class loading failed", e)
                pdfBoxInitializationFailed = true
            }
        }
    }
    
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
            // Initialize PDFBox resources with better error handling
            try {
                PDFBoxResourceLoader.init(context)
            } catch (e: Exception) {
                android.util.Log.w("TextExtractionUtils", "Could not initialize PDFBox resources", e)
                // Continue anyway - some PDFs might still be readable
            }
            
            var document: PDDocument? = null
            try {
                document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                
                // Configure stripper for better text extraction
                stripper.setSortByPosition(true)
                stripper.setSuppressDuplicateOverlappingText(true)
                
                val text = stripper.getText(document)
                
                if (text.isBlank()) {
                    throw Exception("PDF appears to be empty or contains no extractable text")
                }
                
                text.trim()
            } finally {
                document?.close()
            }
        } catch (e: ExceptionInInitializerError) {
            android.util.Log.e("TextExtractionUtils", "PDFBox initialization error - possibly corrupted glyph list", e)
            throw Exception("PDF processing failed due to library initialization error. Please try with a different PDF file or contact support.")
        } catch (e: RuntimeException) {
            // Handle runtime exceptions that might be caused by glyph list issues
            if (e.message?.contains("glyph", ignoreCase = true) == true || 
                e.cause?.message?.contains("glyph", ignoreCase = true) == true) {
                android.util.Log.e("TextExtractionUtils", "Glyph-related error in PDF", e)
                throw Exception("PDF contains unsupported fonts or is corrupted. Please try with a different PDF file.")
            } else {
                android.util.Log.e("TextExtractionUtils", "Runtime error reading PDF", e)
                throw Exception("Error processing PDF: ${e.message}")
            }
        } catch (e: Exception) {
            android.util.Log.e("TextExtractionUtils", "Error reading PDF", e)
            when {
                e.message?.contains("glyph", ignoreCase = true) == true -> {
                    throw Exception("PDF contains unsupported fonts or is corrupted. Please try with a different PDF file.")
                }
                e.message?.contains("password", ignoreCase = true) == true -> {
                    throw Exception("PDF is password protected. Please provide an unprotected PDF file.")
                }
                e.message?.contains("invalid", ignoreCase = true) == true -> {
                    throw Exception("PDF file appears to be corrupted or invalid. Please try with a different PDF file.")
                }
                else -> {
                    throw Exception("Error reading PDF: ${e.message}")
                }
            }
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
