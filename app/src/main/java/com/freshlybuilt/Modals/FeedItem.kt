package com.freshlybuilt.Modals

import org.json.JSONObject

data class FeedItem (

    val image : Int = 0,
    val imageUrl : String = "default",
    val author : String = "default",
    val date : String = "default",
    val description : String = "default",

    val postId : Int = 0 ,
    val title : String = "default",
    val content : String = "default",

    val author_id : Int = 0,
    val author_nickname : String = "default"
)