package com.inplace.chats.data.vk

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.inplace.chats.ChatsRepo
import com.inplace.db.AppDatabase
import com.inplace.models.SuperChat
import com.inplace.models.VKChat
import com.inplace.models.VKUser
import com.inplace.services.ExecutorServices
import com.vk.api.sdk.VK
import java.util.concurrent.ExecutorService

@ExperimentalPagingApi
class VKRepository(val context: Context, val chatsRepo: ChatsRepo = ChatsRepo(context)) {

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 15
        const val PREFER_DISTANCE_SIZE = 9

        //get doggo repository instance
        fun getInstance(context: Context) = VKRepository(context)
    }

    fun letVKChatsLiveData(pagingConfig: PagingConfig = getDefaultPageConfig()): LiveData<PagingData<VKChat>> {
        val db = AppDatabase.getInstance(context)
        val chats = db.getChatsDao()
        return Pager(
            config = pagingConfig,
            remoteMediator = VKMediator(db, chatsRepo),
            pagingSourceFactory = { chats.pagingSource() }
        ).liveData
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, prefetchDistance = PREFER_DISTANCE_SIZE, enablePlaceholders = true)
    }
}

