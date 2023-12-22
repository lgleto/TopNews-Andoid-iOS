package ipca.example.topnews.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.swagger.client.apis.NewsApi
import io.swagger.client.models.Articles
import ipca.example.topnews.Globals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException



sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val code: Int? = null, val error: String? = null) :
        ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()

    inline fun onSuccess(action: (value: T) -> Unit): ResultWrapper<T> {
        if (this is Success) action(value)
        return this
    }

    inline fun onError(action: (error: Error) -> Unit): ResultWrapper<T> {
        if (this is Error) action(this)
        return this
    }

    inline fun onNetworkError(action: () -> Unit): ResultWrapper<T> {
        if (this is NetworkError) action()
        return this
    }
}
private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            "{\"error\" : \"$it\"}"
        }
    } catch (exception: Exception) {
        throwable.message()
    }
}