package com.freshlybuilt.Data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import java.security.AccessControlContext
import java.security.AccessController.getContext

class Preference (context : Context){

        val PREFERENCE_NAME :String ="com.freshlybuilt.SharedPreference"

        val REMEMBER_USER : String = "REMEMBER_USER"
        val COOKIE : String = "USER_COOKIE"
        val ADMIN_COOKIE : String = "ADMIN_COOKIE"
        val USER_NAME :String = "USER_NAME"
        val PASSWORD : String = "PASSWORD"
        val USER_DATA : String = "USER_DATA"

        val PREFERENCE = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
        val edit = PREFERENCE.edit()



    fun storeCookie(cookie : String , adminCookie : String , userName :String , passWord : String ,remember : Int = 0){
        edit.putString(COOKIE, cookie)
        edit.putString(ADMIN_COOKIE, adminCookie)
        edit.putString(USER_NAME, userName)
        edit.putString(PASSWORD, passWord)
        edit.putInt(REMEMBER_USER,remember)
        edit.apply()
    }

    fun authCookie() : String?{
        val cookie = PREFERENCE.getString(COOKIE, null)
        return cookie
    }

    fun rememberMe():Boolean{
        if (PREFERENCE.getInt(REMEMBER_USER,0) ==1){
            return true
        }
        return false
    }

    fun session_save(userData : String){
        edit.putString(USER_DATA,userData)
        edit.apply()
    }

    fun session_retrieve() : String{
        val uData = PREFERENCE.getString(USER_DATA,null).toString()
        return uData
    }

    fun sessionClose(){
        edit.clear()
        edit.apply()
    }

}