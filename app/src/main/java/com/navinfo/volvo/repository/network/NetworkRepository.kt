package com.navinfo.volvo.repository.network

import android.content.Context
import androidx.paging.PagingData
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.network.NetworkDeleteMessagePost
import com.navinfo.volvo.model.network.NetworkMessageListPost
import com.navinfo.volvo.model.network.NetworkMessageListResponse
import com.navinfo.volvo.util.NetResult
import kotlinx.coroutines.flow.Flow

/**
 * 网络访问接口
 */
interface NetworkRepository {
    /**
     * 获取问候列表
     */
    suspend fun getMessageList(message: NetworkMessageListPost): NetResult<DefaultResponse<NetworkMessageListResponse>>

    /**
     *删除问候
     */
    suspend fun deleteMessage(message: NetworkDeleteMessagePost): NetResult<DefaultResponse<*>>

    suspend fun getMessagePaging(
        context: Context, messagePost: NetworkMessageListPost
    ): Flow<PagingData<GreetingMessage>>
}