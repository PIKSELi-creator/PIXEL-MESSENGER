package com.pixelchat.ui.network

import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Creates a Retrofit client for the PIXEL CHAT Spring Boot API.
 *  Pass a base URL ending in '/', e.g. https://your-domain.example/ .
 *  The token provider should return the current bearer token or null.
 */
object PixelChatApiClient {
    fun create(baseUrl: String, tokenProvider: () -> String? = { null }): PixelChatService {
        val normalized = baseUrl.trim().let { if (it.endsWith('/')) it else "$it/" }
        require(normalized.startsWith("https://") || normalized.startsWith("http://127.0.0.1") || normalized.startsWith("http://localhost")) {
            "Use HTTPS in production. Plain HTTP is allowed here only for local development."
        }
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder().header("Accept", "application/json")
            val token = tokenProvider()?.trim().orEmpty()
            if (token.isNotEmpty()) builder.header("Authorization", "Bearer $token")
            chain.proceed(builder.build())
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(normalized)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixelChatService::class.java)
    }
}

interface PixelChatService {
    @GET("api/v1/health") suspend fun health(): Map<String, Any?>

    @POST("api/v1/auth/register") suspend fun register(@Body body: RegisterRequest): BasicApiResponse
    @POST("api/v1/auth/verify-email") suspend fun verifyEmail(@Body body: VerifyEmailRequest): BasicApiResponse
    @POST("api/v1/auth/resend-verification") suspend fun resendVerification(@Body body: ResendVerificationRequest): BasicApiResponse
    @POST("api/v1/auth/login") suspend fun login(@Body body: LoginRequest): LoginResponse
    @POST("api/v1/auth/logout") suspend fun logout(): BasicApiResponse

    @GET("api/v1/profile/me") suspend fun getMyProfile(): ProfileDto
    @PATCH("api/v1/profile/me") suspend fun updateMyProfile(@Body body: UpdateProfileRequest): BasicApiResponse

    @GET("api/v1/users/search") suspend fun searchUsers(@Query("q") query: String): List<UserDto>
    @POST("api/v1/contacts/requests") suspend fun sendContactRequest(@Body body: ContactRequestBody): BasicApiResponse
    @GET("api/v1/contacts") suspend fun getContacts(): List<ContactDto>
    @GET("api/v1/contacts/requests") suspend fun getIncomingContactRequests(): List<ContactDto>
    @POST("api/v1/contacts/requests/{requesterId}/accept") suspend fun acceptContactRequest(@Path("requesterId") requesterId: String): BasicApiResponse
    @DELETE("api/v1/contacts/{userId}") suspend fun removeContact(@Path("userId") userId: String): BasicApiResponse

    @GET("api/v1/chats") suspend fun getChats(): List<ChatDto>
    @POST("api/v1/chats/direct") suspend fun createDirectChat(@Body body: DirectChatRequest): ChatDto
    @POST("api/v1/chats/groups") suspend fun createGroupChat(@Body body: GroupChatRequest): ChatDto
    @POST("api/v1/chats/{chatId}/messages") suspend fun sendMessage(@Path("chatId") chatId: String, @Body body: SendMessageRequest): MessageDto
    @GET("api/v1/chats/{chatId}/messages") suspend fun getMessages(@Path("chatId") chatId: String, @Query("limit") limit: Int = 50): List<MessageDto>
    @POST("api/v1/chats/{chatId}/read") suspend fun markChatRead(@Path("chatId") chatId: String): BasicApiResponse
}

data class RegisterRequest(val email: String, val username: String, val displayName: String, val password: String)
data class VerifyEmailRequest(val email: String, val code: String)
data class ResendVerificationRequest(val email: String)
data class LoginRequest(val identity: String, val password: String)
data class UpdateProfileRequest(val displayName: String? = null, val bio: String? = null, val avatarUrl: String? = null)
data class ContactRequestBody(val username: String)
data class DirectChatRequest(val username: String)
data class GroupChatRequest(val title: String, val usernames: List<String>)
data class SendMessageRequest(val text: String)

data class BasicApiResponse(
    val ok: Boolean? = null,
    val message: String? = null,
    val verified: Boolean? = null,
    val state: String? = null,
    val emailDelivery: String? = null,
    val verificationExpiresInSeconds: Int? = null,
    val markedMessages: Int? = null
)
data class LoginResponse(val accessToken: String, val tokenType: String = "Bearer", val expiresAt: String? = null, val user: LoginUser? = null)
data class LoginUser(val id: String, val username: String, val displayName: String)
data class ProfileDto(val id: String, val email: String, val username: String, val displayName: String, val emailVerified: Boolean, val bio: String = "", val avatarUrl: String = "", val createdAt: String? = null)
data class UserDto(val id: String, val username: String, val displayName: String, val bio: String = "", val avatarUrl: String = "")
data class ContactDto(val id: String, val username: String, val displayName: String)
data class ChatDto(val id: String, val kind: String, val title: String? = null, val createdAt: String? = null)
data class MessageDto(val id: String, val chatId: String, val senderId: String, val text: String, val type: String = "TEXT", val createdAt: String? = null)

/** Produces a safe, user-facing error message from an API failure. */
fun pixelChatErrorMessage(error: Throwable): String {
    if (error is HttpException) {
        val body = try { error.response()?.errorBody()?.string().orEmpty() } catch (_: IOException) { "" }
        if (body.isNotBlank()) {
            try {
                val obj = JsonParser.parseString(body).asJsonObject
                for (key in listOf("message", "detail", "error")) {
                    val value = obj.get(key)
                    if (value != null && !value.isJsonNull && value.isJsonPrimitive) return value.asString
                }
            } catch (_: Exception) { /* Return the HTTP status below. */ }
        }
        return when (error.code()) {
            400 -> "Проверь введённые данные."
            401 -> "Неверный логин или пароль."
            403 -> "Нет доступа или почта ещё не подтверждена."
            404 -> "Не удалось найти пользователя или ресурс."
            409 -> "Такие данные уже используются."
            else -> "Ошибка сервера (${error.code()})."
        }
    }
    return if (error is IOException) "Нет соединения с сервером." else (error.message ?: "Произошла ошибка.")
}
