package com.freshlybuilt.API

data class FBConnect(val FBtoken : String){
    val GET_TOKEN : String = "https://freshlybuilt.com/api/user/fb_connect/?access_token="+FBtoken
}