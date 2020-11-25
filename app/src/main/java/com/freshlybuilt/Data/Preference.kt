package com.freshlybuilt.Data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import java.security.AccessControlContext
import java.security.AccessController.getContext

class Preference (context : Context){

        val PREFERENCE_NAME :String ="com.freshlybuilt.SharedPreference"


        val COOKIE : String = "USER_COOKIE"
        val ADMIN_COOKIE : String = "ADMIN_COOKIE"
        val USER_NAME :String = "USER_NAME"
        val PASSWORD : String = "PASSWORD"

        val PREFERENCE = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
        val edit = PREFERENCE.edit()



    fun storeCookie(cookie : String , adminCookie : String , userName :String , passWord : String){
        edit.putString(COOKIE, cookie)
        edit.putString(ADMIN_COOKIE, adminCookie)
        edit.putString(USER_NAME, userName)
        edit.putString(PASSWORD, passWord)
        edit.apply()
    }

    fun authCookie() : String?{
        val cookie = PREFERENCE.getString(COOKIE, null)
        return cookie
    }

    fun session() : Array<String?>{
        return arrayOf(PREFERENCE.getString(COOKIE, null),
            PREFERENCE.getString(USER_NAME, null),
            PREFERENCE.getString(PASSWORD, null)
        )
    }

    fun sessionClose(){
        edit.clear()
        edit.apply()
    }

}