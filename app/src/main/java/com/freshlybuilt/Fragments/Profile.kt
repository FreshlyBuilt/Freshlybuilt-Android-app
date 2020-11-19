package com.freshlybuilt.Fragments

import android.content.AsyncQueryHandler
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.freshlybuilt.Login

import com.freshlybuilt.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception

class Profile : Fragment(R.layout.fragment_profile) {

    private lateinit var auth : FirebaseAuth

    //private val tokenFB = AccessToken.getCurrentAccessToken()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val user = Firebase.auth.currentUser
        val profileName = user!!.displayName
        val profilePhoto = user!!.photoUrl
        user_name.setText(profileName)
        Glide.with(this).load(profilePhoto).into(user_image)

        log_out.setOnClickListener(){
            logOut()
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
        loginIntent()
    }


}
