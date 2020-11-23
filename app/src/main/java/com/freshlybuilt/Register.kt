package com.freshlybuilt

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        know_more_full_name.setOnClickListener{
            KnowMore("Full_Name_Desc")
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

}
