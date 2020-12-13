package com.inplace.chats

import com.inplace.models.SuperChat
import java.lang.Exception

sealed class RepoResult {
    data class Success(val data: MutableList<SuperChat>) : RepoResult()
    data class Error(val error: Exception) : RepoResult()
}