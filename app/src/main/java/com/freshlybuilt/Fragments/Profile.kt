package com.freshlybuilt.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.freshlybuilt.Base
import com.freshlybuilt.Data.Preference
import com.freshlybuilt.Login
import com.freshlybuilt.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject

class Profile : Fragment(R.layout.fragment_profile) {

    private lateinit var auth : FirebaseAuth
    private val Avatar : String = "https://freshlybuilt.com/api/user/get_avatar/?type='thumb'&user_id="


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        try{
            val jsonParser = JsonParser()
            val userData = JSONObject(Preference(this.activity!!).session_retrieve())
            try{
                val profileName = "@"+userData.getString("username").toLowerCase()
                user_name.setText(profileName)
            }catch (e : Exception){
                val profileName = "@"+userData.getString("nicename").toLowerCase()
                user_name.setText(profileName)
            }
            try{
                val profileMail = userData.getString("email")
                user_email.setText(profileMail)
            }catch (e : Exception){
                user_email.setText("")
            }

            val userID = userData.getString("id")
            val userAvatar = Avatar+userID


            Log.d("profilePhoto",Avatar+userID)
            Glide.with(this).load(userAvatar).into(user_image)
            }catch (e: Exception){
            Log.d("profile",e.toString())
        }

        log_out.setOnClickListener{
            logOut()
            activity!!.finish()
        }
    }


    private fun loginIntent(){
        val iLogin : Intent = Intent(activity,Login::class.java)
        startActivity(iLogin)
    }

    private fun logOut(){
        Firebase.auth.signOut()
        try {
            LoginManager.getInstance().logOut()
        }catch (e : Exception){

        }
        Firebase.auth.signOut()
        try {
            val gsoCall = Login().gso
            val mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gsoCall)
            mGoogleSignInClient.signOut()
        }catch (e : Exception){

        }
        Preference(activity!!).sessionClose()
        loginIntent()
    }


}
