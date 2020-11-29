package com.freshlybuilt.API

import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.Login
import org.json.JSONException
import org.json.JSONObject

class FeedFetchMethod {

    lateinit var responseFetch : JSONObject
    val API_CALL_FETCH_POST : String = "https://freshlybuilt.com/api/get_recent_posts/?page="

    fun PageFetch(page : Int) : StringRequest{
        val requestPostFetch : StringRequest = StringRequest(Request.Method.GET, API_CALL_FETCH_POST+page.toString(),
            Response.Listener<String> { response ->
                try{
                    val responseJson : JSONObject = JSONObject(response)
                    if (responseJson["status"]=="ok"){
                        Log.d("fetch","success")
                        responseFetch = JSONObject(response)
                    }
                    else{
                        Log.d("fetch","failed")
                    }
                }catch (e : JSONException) {
                    Log.d("fetch","exception")
                }
            }, Response.ErrorListener {
                Log.d("fetch","fetch error")
            })
        return requestPostFetch
    }
}