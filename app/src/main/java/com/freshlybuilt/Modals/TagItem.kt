package com.freshlybuilt.Modals

data class TagItem(
    val quesID : Int = 0,
    val quesDATE : String = "default",
    val quesTitle : String = "default",
    val quesContent : String = "default",
    val quesAuthorID : Int = 0,
    val quesCommentStatus : String = "default",
    val quesPingStatus : String = "default"
)