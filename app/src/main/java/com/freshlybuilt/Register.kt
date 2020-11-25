package com.freshlybuilt

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        know_more_full_name.setOnClickListener{
            KnowMore("Full_Name_Desc")
        }

        button_user_register.setOnClickListener {
            if (Full_Name_text.text!!.isEmpty() ||
                    user_name_text.text!!.isEmpty() ||
                    email_text.text!!.isEmpty() ||
                        password_text.text!!.isEmpty() ||
                        c_password_text.text!!.isEmpty()){
            }else{

                if (c_password_text.text.toString() != password_text.text.toString()){

                }else{
                    UserFirebaseAuth()
                }
            }
        }
    }

    private fun KnowMore(know_more_id : String){
        if (know_more_id == "Full_Name_Desc"){
            if (Full_Name_Desc.visibility == View.GONE){
                know_more_full_name.setText("*Know Less ")
                TransitionManager.beginDelayedTransition(Full_Name,AutoTransition())
                Full_Name_Desc.visibility = View.VISIBLE
            }
            else{
                know_more_full_name.setText("*Know More ")
                TransitionManager.beginDelayedTransition(Full_Name,AutoTransition())
                Full_Name_Desc.visibility = View.GONE
            }
        }

    }

    private fun UserFirebaseAuth(){
        val auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email_text.text.toString(), password_text.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception.toString(),
                        Toast.LENGTH_LONG).show()
                    //updateUI(null)
                }

                // ...
            }

    }

}
