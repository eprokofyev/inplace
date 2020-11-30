package com.inplace.chats

import com.inplace.models.Chat
import java.lang.Exception

sealed class RepoResult {
    data class Success(val data: MutableList<Chat>) : RepoResult()
    data class Error(val error: Exception) : RepoResult()
}