package com.navinfo.volvo.repository

import com.navinfo.volvo.di.scope.IoDispatcher
import com.navinfo.volvo.model.Message
import com.navinfo.volvo.model.network.NetworkPostMessage
import com.navinfo.volvo.repository.service.NetworkService
import com.navinfo.volvo.tools.GsonUtil
import com.navinfo.volvo.util.NetResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class NetworkDataSourceImp @Inject constructor(
    private val netWorkService: NetworkService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NetworkDataSource {

    override suspend fun getCardList(message: NetworkPostMessage): NetResult<List<Message>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val stringBody = GsonUtil.getInstance().toJson(message).toRequestBody("application/json;charset=utf-8".toMediaType())
                val result = netWorkService.queryCardListByApp(stringBody)
                if (result.isSuccessful) {
                    val list = result.body()
                    NetResult.Success(list)
                } else {
                    NetResult.Success(null)
                }
            } catch (e: Exception) {
                NetResult.Error(e)
            }
        }


}