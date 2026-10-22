package com.managementtution.app.network

import com.managementtution.app.model.ApiHealthResponse
import retrofit2.Response
import retrofit2.http.GET

interface TuitionApiService {
    @GET("health")
    suspend fun checkHealth(): Response<ApiHealthResponse>
}
