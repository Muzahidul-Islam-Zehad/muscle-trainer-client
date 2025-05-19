package com.example.muscletrainer.network

import com.example.muscletrainer.model.PersonalInfo
import com.example.muscletrainer.model.User
import com.example.muscletrainer.model.Workout
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("/users1")
    suspend fun getUsers() : List<User>
    @GET("/personal-info")
    suspend fun getPersonalInfo(@Query("email") email: String): PersonalInfo

    @GET("/check-userInfo")
    suspend fun checkUserInfoExists(@Query("email") email: String): Boolean

    @GET("/workout_plan")
    suspend fun getWorkoutsByType(@Query("w_type") type: String): List<Workout>


    @POST("/add-user")
    suspend fun createUser(@Body user: User) : Response<Unit>

    @POST("/add-personal-info")
    suspend fun setPersonalInfo(@Body personalInfo: PersonalInfo) : Response<Unit>
}