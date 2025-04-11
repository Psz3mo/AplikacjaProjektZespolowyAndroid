package com.pwrpower.apk.api

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("x-api-key", "f5c51a2d-dd33-46f0-b3ab-2dd75dc0c1f1") // IT IS NOT A REAL API KEY !!!
            .build()

        return chain.proceed(modifiedRequest)
    }
}