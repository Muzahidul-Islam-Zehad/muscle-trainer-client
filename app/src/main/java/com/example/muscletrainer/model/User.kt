package com.example.muscletrainer.model

import java.time.Instant

data class User( val userName: String,val email: String ,val location : String)


//private fun createUser(user: User) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val response = RetrofitInstance.api.createUser(user)
//            withContext(Dispatchers.Main) {
//                println("Create user status: ${response.code()}")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}