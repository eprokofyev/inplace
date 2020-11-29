package com.inplace.auth.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User_VK (
    val userId: String,
    val displayName: String
)