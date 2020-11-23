package com.freshlybuilt

import android.R.attr
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.util.*


class Login : AppCompatActivity() {
    private val RC_SIGN_IN : Int = 0
    private lateinit var callbackManager : CallbackManager
    private lateinit var auth: FirebaseAuth
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("508076055199-7ilma27j1tk7iqguu9kfgs9mtuvuvoe6.apps.googleusercontent.com").requestEmail().build()
    private val tokenFB =AccessToken.getCurrentAccessToken()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null){
            baseIntent()
        }
    }

    override fun onResume() {
        super.onResume()
        button_google_login.setOnClickListener{
            signInWithGoogle()
        }

        button_facebook_custom.setOnClickListener{
            signInWithFacebook()
        }

        Button_Manual_Register.setOnClickListener {
            registerIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun signInWithGoogle(){
        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }



    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account.idToken == null){
                inAppNotification("account token is null")
            }
            else{
                firebaseAuthWithGoogle(account.idToken!!)
                baseIntent()
            }

        } catch (e: ApiException) {
            inAppNotification(e.statusCode.toString())
        }
    }

    private fun signInWithFacebook(){
        button_facebook_login.performClick()
        callbackManager = CallbackManager.Factory.create()
        button_facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                //inAppNotification("login successful")
                handleFacebookAccessToken(AccessToken.getCurrentAccessToken())
            }
            override fun onCancel() {
                inAppNotification("login Cancel")
            }
            override fun onError(error: FacebookException?) {
                inAppNotification(error.toString())
            }
        })
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    baseIntent()
                } else {
                    LoginManager.getInstance().logOut()
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun baseIntent(){
        val iBase : Intent = Intent(this, Base::class.java)
        startActivity(iBase)
    }

    private fun registerIntent(){
        val iRegister : Intent = Intent(this, Register::class.java)
        startActivity(iRegister)
    }



    private fun inAppNotification(arg :String){
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,arg,tTime)
        toast.show()
    }
}
