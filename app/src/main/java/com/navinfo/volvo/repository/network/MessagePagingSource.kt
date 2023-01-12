package com.navinfo.volvo.repository.network

import android.content.Context
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.navinfo.volvo.Constant
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.model.network.NetworkMessageListPost
import com.navinfo.volvo.tools.GsonUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MessagePagingSource(
    private val networkService: NetworkService,
    private val context: Context,
    private val messagePost: NetworkMessageListPost,
) : PagingSource<Int, GreetingMessage>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GreetingMessage> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            messagePost.pageSize = "${Constant.MESSAGE_PAGE_SIZE}"
            messagePost.pageNum = "$page";

            val stringBody = GsonUtil.getInstance().toJson(messagePost)
                .toRequestBody("application/json;charset=utf-8".toMediaType())
            val result = networkService.queryMessageListByApp(stringBody)
            var list: List<GreetingMessage> = emptyList()
            if (result.isSuccessful) {
                val body = result.body();
                body?.let {
                    if (it.code == 200) {
                        val data = it.data
                        data?.let { d ->
                            if (d.rows != null) {
                                list = d.rows
//                                for (item in list){
//                                    if(item.version == Constant.message_version_right_off && item.status == Constant.message_status_late){
//                                        item.status = Constant.message_status_send_over
//                                    }
//                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "${it.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "${result.message()}", Toast.LENGTH_SHORT).show()
            }

            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (list.isNotEmpty()) page + 1 else null
            LoadResult.Page(list, prevKey, nextKey)

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GreetingMessage>): Int? {
        return null
    }
}