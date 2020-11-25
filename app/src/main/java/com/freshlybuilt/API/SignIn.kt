package com.freshlybuilt.API

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.Login
import org.json.JSONException
import org.json.JSONObject

class SignIn {

    var AUTH_RESPONSE : Boolean = false
    lateinit var JSON_COOKIE_GENERATE_RESPONSE : JSONObject

    val cookie_auth_address = "https://freshlybuilt.com/api/user/validate_auth_cookie/?cookie="


     fun CookieGenerateUrl(userName : String , userPassword : String) : String {
        return "https://freshlybuilt.com/api/user/generate_auth_cookie/?username=${userName}&password=${userPassword}"
    }

    fun CookieGenerateRequest(userName : String , userPassword : String):StringRequest{
        val cookie_generate_request = StringRequest(Request.Method.GET, CookieGenerateUrl(userName,userPassword),
            Response.Listener<String> { response ->
                try{
                    val userJson : JSONObject = JSONObject(response)
                    if (userJson["status"]=="ok"){
                        this.JSON_COOKIE_GENERATE_RESPONSE = userJson
                    }
                    else{
                        Login().inAppNotification("status not OK")
                    }
                }catch (e : JSONException) {
                    Login().inAppNotification(e.toString())
                }
            }, Response.ErrorListener {})

        return cookie_generate_request
    }

    fun AuthenticateCookie(cookie :String) : StringRequest{
        val apiCall : String = cookie_auth_address+cookie
        Log.d("authCookie",apiCall)
        val cookie_auth_request = StringRequest(Request.Method.GET, apiCall,
        Response.Listener<String> { response ->
            try{
                val userJson : JSONObject = JSONObject(response)
                if (userJson["status"]=="ok" && userJson["valid"]=="true"){

                    this.AUTH_RESPONSE = true
                }else{
                    Log.d("cookie invalid","invalid cookie")
                    Log.d("usercookie",userJson.toString())
                }
                }catch (e : JSONException) {
            }
        }, Response.ErrorListener {})

        return cookie_auth_request
    }

}