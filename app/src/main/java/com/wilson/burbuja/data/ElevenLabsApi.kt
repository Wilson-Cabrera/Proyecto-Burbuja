package com.wilson.burbuja.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ElevenLabsApi {
    @Streaming
    @POST("text-to-speech/{voice_id}")
    suspend fun generarAudio(
        @Path("voice_id") voiceId: String,
        @Header("xi-api-key") apiKey: String = ElevenLabsConfig.API_KEY,
        @Body request: TTSRequest
    ): Response<ResponseBody>
}