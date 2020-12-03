package com.freshlybuilt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.freshlybuilt.API.FBConnect
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class Login : AppCompatActivity() {

    private val userSignIn = SignIn()
    private val RC_SIGN_IN : Int = 0
    private lateinit var callbackManager : CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var requestQueue: RequestQueue
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("508076055199-7ilma27j1tk7iqguu9kfgs9mtuvuvoe6.apps.googleusercontent.com").requestEmail().build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        requestQueue = Volley.newRequestQueue(this)
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
        progress("logging in...")
        button_facebook_login.setPermissions(Arrays.asList("email"))
        button_facebook_login.performClick()
        callbackManager = CallbackManager.Factory.create()
        button_facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                progress("fetching data...")
                val token : String = result!!.accessToken.token
                Log.d("fbtoken",token)
                val fbConnect : String = FBConnect(token).GET_TOKEN
                Log.d("fbconect",fbConnect)
                val requestFBConnect = StringRequest(Request.Method.GET,fbConnect,
                Response.Listener<String> { responseFBConnect ->
                    try{
                        val responseJson : JSONObject = JSONObject(responseFBConnect)
                        if (responseJson["msg"]=="user logged in."){
                            Log.d("fetch","success")
                            val data = JSONObject(responseFBConnect)
                            val cookie = data.getString("cookie")
                            val userID = data.getString("wp_user_id")
                            val userDetails = "https://freshlybuilt.com/api/user/get_userinfo/?user_id="+userID
                            val requestUserData = StringRequest(Request.Method.GET,userDetails,
                                Response.Listener<String> { responseUD ->
                                    try{
                                        val responseJson : JSONObject = JSONObject(responseUD)
                                        if (responseJson["status"]=="ok"){
                                            Log.d("fetch","success")
                                            val data = JSONObject(responseUD)
                                            val  preference : Preference = Preference(this@Login)
                                            preference.storeCookieFB(cookie)
                                            preference.session_save(data.toString())
                                            progress("",true)
                                            baseIntent()
                                            finish()
                                        }
                                        else{
                                            Log.d("fetch","failed")
                                        }
                                    }catch (e : JSONException) {
                                        Log.d("fetch",e.toString())
                                    }
                                }, Response.ErrorListener {
                                    Log.d("fetch","fetch error")
                                })
                            requestQueue.add(requestUserData)
                        }
                        else{
                            Log.d("fetch","failed")
                        }
                    }catch (e : JSONException) {
                        Log.d("fetch",e.toString())
                    }
                }, Response.ErrorListener {
                    Log.d("fetch","fetch error")
                })
                requestQueue.add(requestFBConnect)

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
                    val user = auth.currentUser

                } else {

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
            progress("logging in")
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
                    catch (e : Exception){ Log.d("session","session save fail"+ e.toString())}
                    Log.d("data",preference.session_retrieve())
                    inAppNotification("login successful")
                    progress("logged in",true)
                    baseIntent()
                    finish()
                }catch (e :Exception){
                    Log.d("login" , e.toString())
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

    private fun progress(msg : String , isClose : Boolean = false){
        if (!isClose){
            TransitionManager.beginDelayedTransition(login_progress_card, AutoTransition())
            progress_bar.visibility = View.VISIBLE
            progress_text_login.visibility = View.VISIBLE
            progress_text_login.setText(msg)
        }else{
            progress_text_login.setText(msg)
            progress_bar.visibility=View.GONE
            progress_text_login.visibility = View.GONE
        }

    }

}
