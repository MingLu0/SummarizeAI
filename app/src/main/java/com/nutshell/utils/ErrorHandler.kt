package com.nutshell.utils

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun handleError(error: Throwable, userMessage: String? = null) {
        val message = when (error) {
            is java.net.UnknownHostException -> "Network not available. Please check your internet connection."
            is java.net.SocketTimeoutException -> "Request timed out. Please try again."
            is java.io.IOException -> "Network error. Please try again."
            is SecurityException -> "Permission denied. Please grant necessary permissions."
            is IllegalArgumentException -> "Invalid input. Please check your data."
            is UnsupportedOperationException -> "This operation is not supported."
            else -> userMessage ?: "An unexpected error occurred. Please try again."
        }
        
        showErrorToast(message)
    }
    
    fun showErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    fun showSuccessToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    fun showInfoToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is java.net.UnknownHostException -> "Network not available"
            is java.net.SocketTimeoutException -> "Request timed out"
            is java.io.IOException -> "Network error"
            is SecurityException -> "Permission denied"
            is IllegalArgumentException -> "Invalid input"
            is UnsupportedOperationException -> "Operation not supported"
            else -> error.message ?: "Unknown error occurred"
        }
    }
}
