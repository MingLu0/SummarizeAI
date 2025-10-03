package com.summarizeai.utils

import android.content.Context
import android.net.Uri
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class TextExtractionUtilsTest {
    
    private lateinit var textExtractionUtils: TextExtractionUtils
    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk()
        
        textExtractionUtils = TextExtractionUtils(context)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `extractTextFromUri with PDF should return success`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val inputStream = mockk<InputStream>()
        val fileName = "test.pdf"
        
        mockkStatic("com.tom_roush.pdfbox.pdmodel.PDDocument")
        mockkStatic("com.tom_roush.pdfbox.text.PDFTextStripper")
        
        every { context.contentResolver.openInputStream(uri) } returns inputStream
        every { context.contentResolver.query(uri, any(), any(), any(), any()) } returns mockk {
            every { getColumnIndex(any()) } returns 0
            every { moveToFirst() } returns true
            every { getString(0) } returns fileName
            every { close() } just Runs
        }
        
        // Mock PDF processing
        val mockDocument = mockk<com.tom_roush.pdfbox.pdmodel.PDDocument>()
        val mockStripper = mockk<com.tom_roush.pdfbox.text.PDFTextStripper>()
        
        mockkStatic("com.tom_roush.pdfbox.pdmodel.PDDocument")
        mockkStatic("com.tom_roush.pdfbox.text.PDFTextStripper")
        
        every { com.tom_roush.pdfbox.pdmodel.PDDocument.load(inputStream) } returns mockDocument
        every { com.tom_roush.pdfbox.text.PDFTextStripper() } returns mockStripper
        every { mockStripper.getText(mockDocument) } returns "Extracted PDF text"
        every { mockDocument.close() } just Runs
        every { inputStream.close() } just Runs
        
        // When
        val result = textExtractionUtils.extractTextFromUri(uri)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals("Extracted PDF text", result.getOrNull())
    }
    
    @Test
    fun `extractTextFromUri with unsupported file type should return failure`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val inputStream = mockk<InputStream>()
        val fileName = "test.txt"
        
        every { context.contentResolver.openInputStream(uri) } returns inputStream
        every { context.contentResolver.query(uri, any(), any(), any(), any()) } returns mockk {
            every { getColumnIndex(any()) } returns 0
            every { moveToFirst() } returns true
            every { getString(0) } returns fileName
            every { close() } just Runs
        }
        
        every { inputStream.close() } just Runs
        
        // When
        val result = textExtractionUtils.extractTextFromUri(uri)
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UnsupportedOperationException)
    }
    
    @Test
    fun `extractTextFromUri with null input stream should return failure`() = runTest {
        // Given
        val uri = mockk<Uri>()
        
        every { context.contentResolver.openInputStream(uri) } returns null
        
        // When
        val result = textExtractionUtils.extractTextFromUri(uri)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Could not open file", result.exceptionOrNull()?.message)
    }
}
