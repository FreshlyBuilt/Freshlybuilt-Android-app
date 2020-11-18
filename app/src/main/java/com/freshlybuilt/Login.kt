package com.freshlybuilt

import android.R.attr
import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.util.*


class Login : AppCompatActivity() {
    private val RC_SIGN_IN : Int = 0
    private lateinit var callbackManager : CallbackManager
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
    private val tokenFB =AccessToken.getCurrentAccessToken()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*val fbLogin = button_facebook_login
        callbackManager = CallbackManager.Factory.create()
        fbLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                inAppNotification("login successful")
            }
            override fun onCancel() {
                inAppNotification("login Cancel")
            }
            override fun onError(error: FacebookException?) {
                inAppNotification(error.toString())
            }
        })*/

    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null && tokenFB ==null){
            inAppNotification("please login to continue")
        }
        if (account !=null){
            baseIntent()
        }
        if (tokenFB != null) {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }else{

            //baseIntent()
        }*/
    }

    private fun signInWithGoogle(){
        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun signInWithFacebook(){
        button_facebook_login.performClick()
        callbackManager = CallbackManager.Factory.create()
        button_facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                inAppNotification("login successful")
            }
            override fun onCancel() {
                inAppNotification("login Cancel")
            }
            override fun onError(error: FacebookException?) {
                inAppNotification(error.toString())
            }
        })
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            baseIntent()
        } catch (e: ApiException) {
            inAppNotification(e.statusCode.toString())
        }
    }

    private fun baseIntent(){
        val iBase : Intent = Intent(this, Base::class.java)
        startActivity(iBase)
    }

    private fun inAppNotification(arg :String){
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,arg,tTime)
        toast.show()
    }
}
