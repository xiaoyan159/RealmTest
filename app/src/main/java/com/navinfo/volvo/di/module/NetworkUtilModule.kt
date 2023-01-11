package com.navinfo.volvo.di.module

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.navinfo.volvo.Constant
import com.navinfo.volvo.repository.network.NetworkService
import com.navinfo.volvo.tools.GsonUtil
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkUtilModule {


    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (Constant.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: Lazy<OkHttpClient>,
        converterFactory: GsonConverterFactory,
//        context: Context
    ): Retrofit {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(Constant.SERVER_ADDRESS)
            .client(client.get())
            .addConverterFactory(converterFactory)

//        val okHttpClientBuilder = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//
//                val original = chain.request()
//                val originalHttpUrl = original.url
//
//                val url = originalHttpUrl.newBuilder()
//                    .addQueryParameter("appid", BuildConfig.API_KEY)
//                    .build()
//
//                Timber.d("Started making network call")
//
//                val requestBuilder = original.newBuilder()
//                    .url(url)
//
//                val request = requestBuilder.build()
//                return@addInterceptor chain.proceed(request)
//            }
//            .readTimeout(60, TimeUnit.SECONDS)
//        if (Constant.DEBUG) {
//            okHttpClientBuilder.addInterceptor(ChuckInterceptor(context))
//        }
//        return retrofitBuilder.client(okHttpClientBuilder.build()).build()
        return retrofitBuilder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonUtil.getInstance()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideNetworkService(retrofit: Retrofit): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }

}
