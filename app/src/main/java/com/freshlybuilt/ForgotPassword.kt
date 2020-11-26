package com.freshlybuilt

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.API.ForgotPasswordMethod
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.lang.Exception

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        button_username_or_email_submit.setOnClickListener{
            dismissKeyboard(this)
            if (!user_name_or_email_text.text.isNullOrEmpty()){
                forgot_password_progress.visibility = View.VISIBLE
                val requestQueue = Volley.newRequestQueue(this)
                requestQueue.add(ForgotPasswordMethod().UserPasswordRecovery(user_name_or_email_text.text.toString()))
                requestQueue.addRequestFinishedListener<StringRequest> {
                      try{
                          showDialogue()
                      } catch (e : Exception)
                      {
                          Log.d("dialog",e.toString())
                      }

                }
            }else{

            }
        }


    }


    private fun dismissKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (null != activity.currentFocus) imm.hideSoftInputFromWindow(
            activity.currentFocus!!
                .applicationWindowToken, 0
        )
    }

    private fun showDialogue(){
       val dialogueInflator = LayoutInflater.from(this)
        val view = dialogueInflator.inflate(R.layout.alert_forget_password,null)
        val  retryButton = view.findViewById<View>(R.id.retry_button)


        val  loginButton = view.findViewById<View>(R.id.go_to_login_screen_button)
        val alertDialog = AlertDialog.Builder(this).setView(view).create()
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

        retryButton.setOnClickListener{
            alertDialog.cancel()
        }
        loginButton.setOnClickListener{
            alertDialog.cancel()
            loginIntent()
        }
    }

    private fun loginIntent(){
        val intentLogin = Intent(this,Login::class.java)
        startActivity(intentLogin)
    }
}
