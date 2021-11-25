package com.lilcode.aop.p4c03.googlemap.utility

import com.lilcode.aop.p4c03.googlemap.BuildConfig
import com.lilcode.aop.p4c03.googlemap.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtil {

    val apiService: ApiService by lazy { getRetrofit().create(ApiService::class.java) }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Url.TMAP_URL)
            .addConverterFactory(GsonConverterFactory.create()) // gson으로 파싱
            .client(buildOkHttpClient()) // OkHttp 사용
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor() // 매번 api 호출 시 마다 로그 확인 할것
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS) // 5초 동안 응답 없으면 에러
            .addInterceptor(interceptor)
            .build()
    }
}