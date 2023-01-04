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
    data class Error(val exception: Exception) : NetResult<Nothing>()
    object Loading : NetResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading"
        }
    }
}
