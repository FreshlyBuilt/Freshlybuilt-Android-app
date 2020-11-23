package com.freshlybuilt.API


object SignUp{

    private fun checkUname(user_name: String){

    }

    private fun checkPassword(user_name: String){

    }

    private fun callNonce() :String{
        return "https://freshlybuilt.com/api/get_nonce/?controller=user&method=register"
    }

    private fun callRegister (nonce : String ,
                              uName :String ,
                              eMail: String ,
                              dispName : String,
                              notify : String = "both") : String{
        return "https://freshlybuilt.com/api/user/register/" +
                "?username=${uName}" +
                "&email=$eMail" +
                "&nonce=$nonce" +
                "&display_name=${dispName}" +
                "&notify=${notify}"
    }
}

