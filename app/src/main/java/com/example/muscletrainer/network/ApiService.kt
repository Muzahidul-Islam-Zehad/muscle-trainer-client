package com.example.muscletrainer.network

import com.example.muscletrainer.model.PersonalInfo
import com.example.muscletrainer.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/users1")
    suspend fun getUsers() : List<User>

    @POST("/add-user")
    suspend fun createUser(@Body user: User) : Response<Unit>

    @POST("/add-personal-info")
    suspend fun setPersonalInfo(@Body personalInfo: PersonalInfo) : Response<Unit>
}