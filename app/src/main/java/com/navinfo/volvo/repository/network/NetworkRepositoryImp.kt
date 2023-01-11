package com.navinfo.volvo.repository.network

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.navinfo.volvo.Constant
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.di.scope.IoDispatcher
import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.network.NetworkDeleteMessagePost
import com.navinfo.volvo.model.network.NetworkMessageListPost
import com.navinfo.volvo.model.network.NetworkMessageListResponse
import com.navinfo.volvo.util.NetResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class NetworkRepositoryImp @Inject constructor(
    private val netWorkService: NetworkService,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NetworkRepository {


    /**
     * 获取问候列表
     */
    override suspend fun getMessageList(message: NetworkMessageListPost): NetResult<DefaultResponse<NetworkMessageListResponse>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val stringBody = gson.toJson(message)
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                val result = netWorkService.queryMessageListByApp(stringBody)
                if (result.isSuccessful) {
                    if (result.body()!!.code == 200) {
                        NetResult.Success(result.body())
                    } else {
                        NetResult.Failure(result.body()!!.code, result.body()!!.msg)
                    }
                } else {
                    NetResult.Success(null)
                }
            } catch (e: Exception) {
                NetResult.Error(e)
            }
        }

    /**
     * 删除问候
     */
    override suspend fun deleteMessage(message: NetworkDeleteMessagePost): NetResult<DefaultResponse<*>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val stringBody = gson.toJson(message)
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                val result = netWorkService.deleteMessage(stringBody)
                if (result.isSuccessful) {
                    if (result.body()!!.code == 200) {
                        NetResult.Success(result.body())
                    } else {
                        NetResult.Failure(result.body()!!.code, result.body()!!.msg)
                    }
                } else {
                    NetResult.Success(null)
                }
            } catch (e: Exception) {
                NetResult.Error(e)
            }
        }

    override suspend fun getMessagePaging(
        context: Context, messagePost: NetworkMessageListPost
    ): Flow<PagingData<GreetingMessage>> {
        return Pager(config = PagingConfig(
            pageSize = Constant.MESSAGE_PAGE_SIZE,
            prefetchDistance = 1
        ), pagingSourceFactory = {
            MessagePagingSource(
                netWorkService, context, messagePost
            )
        }).flow
    }

}