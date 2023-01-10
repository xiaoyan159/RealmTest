package com.navinfo.volvo.util

/**
 * Created by Mayokun Adeniyi on 23/05/2020.
 */

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class NetResult<out R> {

    data class Success<out T>(val data: T?) : NetResult<T>()
    data class Failure(val code: Int, val msg: String) : NetResult<Nothing>()
    data class Error(val exception: Exception) : NetResult<Nothing>()
    object Loading : NetResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "网络访问成功，返回正确结果Success[data=$data]"
            is Failure -> "网络访问成功，返回错误结果Failure[$msg]"
            is Error -> "网络访问出错 Error[exception=$exception]"
            is Loading -> "网络访问中 Loading"
        }
    }
}
