package com.freshlybuilt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.freshlybuilt.API.SignIn
import com.freshlybuilt.Data.Preference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    private val userSignIn = SignIn()
    private val RC_SIGN_IN : Int = 0
    private lateinit var callbackManager : CallbackManager
    private lateinit var auth: FirebaseAuth
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("508076055199-7ilma27j1tk7iqguu9kfgs9mtuvuvoe6.apps.googleusercontent.com").requestEmail().build()
    private val tokenFB =AccessToken.getCurrentAccessToken()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val requestQueue = Volley.newRequestQueue(this)
        val preference = Preference(this)
        requestQueue.add(userSignIn.AuthenticateCookie(preference.authCookie().toString()))
    }

    override fun onStart() {
        super.onStart()
        val requestQueue = Volley.newRequestQueue(this)
        val preference = Preference(this)
        val currentUser = auth.currentUser
        if ( preference.authCookie()!= null){
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

        Button_Manual_Login.setOnClickListener {
            dismissKeyboard(this)
            progress_bar.visibility = View.VISIBLE
            UserFirebaseLogin()
            ManualLogin(user_name_login_text.text.toString(),user_password_login_text.text.toString())
        }

        forgot_password.setOnClickListener{
            forgotIntent()
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

    private fun forgotIntent(){
        val iForgot : Intent = Intent(this, ForgotPassword::class.java)
        startActivity(iForgot)
    }

    fun inAppNotification(arg :String){
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,arg,tTime)
        toast.show()
    }

    private fun ManualLogin(userName : String, passWord : String){
        if ( userName.isEmpty() || passWord.isEmpty()  ){
            inAppNotification("oops !! something is missing")
        }
        else{
            val preference = Preference(this)
            val requestQueue = Volley.newRequestQueue(this)
            val apiCall : StringRequest = userSignIn.CookieGenerateRequest(userName,passWord)
            requestQueue.add(apiCall)
            requestQueue.addRequestFinishedListener<StringRequest> {
                try{ if (remember_me.isChecked){
                    preference.storeCookie(userSignIn.JSON_COOKIE_GENERATE_RESPONSE["cookie"].toString(),
                        userSignIn.JSON_COOKIE_GENERATE_RESPONSE["cookie_admin"].toString(),
                        userName,passWord,1) }
                    else{
                    preference.storeCookie(userSignIn.JSON_COOKIE_GENERATE_RESPONSE["cookie"].toString(),
                        userSignIn.JSON_COOKIE_GENERATE_RESPONSE["cookie_admin"].toString(),
                        userName,passWord,0) }

                    try{preference.session_save(userSignIn.JSON_USER_RETRIEVED_DATA.toString())}
                    catch (e : Exception){Log.d("session","session save fail")}

                    Log.d("data",preference.session_retrieve().toString())
                    inAppNotification("login successful")
                    baseIntent()
                }catch (e :Exception){
                    inAppNotification(e.toString())
                }
                progress_bar.visibility = View.GONE
            }
        }
    }

    private fun UserFirebaseLogin(){
       /* auth.signInWithEmailAndPassword(email_text.text.toString(), user_password_text.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                    // ...
                }

                // ...
            } */
    }
    fun dismissKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (null != activity.currentFocus) imm.hideSoftInputFromWindow(
            activity.currentFocus!!
                .applicationWindowToken, 0
        )
    }

}
