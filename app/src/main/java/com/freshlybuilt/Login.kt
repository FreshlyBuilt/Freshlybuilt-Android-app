package com.freshlybuilt

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
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
import java.util.*


class Login : AppCompatActivity() {
    private val RC_SIGN_IN : Int = 0

    val callbackManager = CallbackManager.Factory.create()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val button_facebook_Login : LoginButton = button_facebook_login

        //button_facebook_Login.setPermissions(Arrays.asList("user_gender","user_friends"))
        button_facebook_Login.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                inAppNotification("login successful")
            }

            override fun onCancel() {
                inAppNotification("login Cancel")
            }

            override fun onError(error: FacebookException?) {
                inAppNotification("oops! something went wrong")
            }
        })

    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null){
            inAppNotification("please login to continue")
        }
        else{
            val iBase : Intent = Intent(this, Base::class.java)
            startActivity(iBase)
        }
    }

    override fun onResume() {
        super.onResume()
        button_google_login.setOnClickListener{
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }else{

        }*/
    }

    private fun signIn(){
        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val iBase : Intent = Intent(this, Base::class.java)
            startActivity(iBase)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(FragmentActivity.TAG, "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
            inAppNotification(e.statusCode.toString())
        }
    }

    private fun inAppNotification(arg :String){
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,arg,tTime)
        toast.show()
    }
}
