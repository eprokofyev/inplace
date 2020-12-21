package com.inplace.chat

import com.inplace.models.Message
import java.lang.Exception

sealed class ChatRepoResult {
    data class Success(val data: List<Message>) : ChatRepoResult()
    data class Error(val error: Exception) : ChatRepoResult()
}