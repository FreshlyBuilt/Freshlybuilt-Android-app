package com.freshlybuilt.Fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.freshlybuilt.Login

import com.freshlybuilt.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.fragment_profile.*

class Profile : Fragment(R.layout.fragment_profile) {



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



    }

    override fun onStart() {
        super.onStart()
        val userAcc = GoogleSignIn.getLastSignedInAccount(activity)
        var profileName = userAcc!!.displayName
        var profilePhoto = userAcc!!.photoUrl
        user_name.setText(profileName)
        Glide.with(this).load(profilePhoto).into(user_image)



        log_out.setOnClickListener(){
            signOut()
        }

    }

    private fun signOut(){
        val gsoCall = Login().gso
        val mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gsoCall)
        val iLogin : Intent = Intent(activity,Login::class.java)
        mGoogleSignInClient.signOut()
        startActivity(iLogin)

    }


}
