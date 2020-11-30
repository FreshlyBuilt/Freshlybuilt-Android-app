package com.freshlybuilt.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.fragment.app.Fragment
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

class Profile : Fragment(R.layout.fragment_profile) {

    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        try{val jsonParser = JsonParser()
            val userData  = jsonParser.parse(Preference(this.activity!!).session_retrieve()).asJsonObject
            val profileName = userData["username"].toString()
            user_name.setText(profileName)

            //val profilePhoto = user!!.photoUrl
            //Glide.with(this).load(profilePhoto).into(user_image)
            }catch (e: Exception){

            //val pref: SharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
            val id: String? = Preference(activity!!).session_retrieve()
            //TVauth.setText(id.toString())
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
