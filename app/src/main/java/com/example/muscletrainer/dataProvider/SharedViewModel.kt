package com.example.muscletrainer.dataProvider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.muscletrainer.model.PersonalInfo
import com.example.muscletrainer.model.User

class SharedViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user
    private val _personalInfo = MutableLiveData<PersonalInfo>()
    val personalInfo : LiveData<PersonalInfo> get() = _personalInfo



    fun setUserViewModel(user: User) {
        _user.value = user
    }
    fun setPersonalInfoViewModel(info: PersonalInfo) {
        _personalInfo.value = info
    }
//    fun clearViewModel() {
//        _user.value = null
//        _personalInfo.value = null
//    }
}