import retrofit2.Call
import retrofit2.http.*

data class UserRequest(
    val email: String,
    val password: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null
)

data class UserResponse(
    val message: String,
    val email: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null
)

interface ApiService {

    @POST("/signup")
    fun signup(@Body request: UserRequest): Call<UserResponse>

    @POST("/login")
    fun login(@Body request: UserRequest): Call<UserResponse>

    @POST("/update_profile")
    fun updateProfile(@Body request: UserRequest): Call<UserResponse>

    @GET("/get_user")
    fun getUser(@Query("email") email: String): Call<UserResponse>
}
