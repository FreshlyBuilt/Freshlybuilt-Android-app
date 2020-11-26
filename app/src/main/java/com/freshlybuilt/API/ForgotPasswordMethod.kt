package com.freshlybuilt.API

import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.freshlybuilt.Login
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordMethod {

    val API_CALL_FORGOT_PASSWORD : String = "https://freshlybuilt.com/api/user/retrieve_password/?user_login="

    fun UserPasswordRecovery(userCredential : String) : StringRequest{
        val userPasswordRecoveryRequest : StringRequest = StringRequest(Request.Method.GET, API_CALL_FORGOT_PASSWORD+userCredential,
            Response.Listener<String> { response ->
                try{
                    val responseJson : JSONObject = JSONObject(response)
                    if (responseJson["status"]=="ok"){
                        //Login().inAppNotification("check your mail inbox for password recovery email")
                    }
                    else{
                        //Login().inAppNotification("invalid username or email")
                    }
                }catch (e : JSONException) {
                    Login().inAppNotification("json_user_retrieve_failed")
                    Login().inAppNotification(e.toString())
                }
            }, Response.ErrorListener {
                Log.d("recover","recovery error")
            })

        return userPasswordRecoveryRequest
    }
}