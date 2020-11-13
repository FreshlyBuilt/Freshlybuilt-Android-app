package com.freshlybuilt

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freshlybuilt.Fragments.Feed
import com.freshlybuilt.Fragments.Login
import com.freshlybuilt.Fragments.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_base.*


class Base : AppCompatActivity() {
    private val fLogin = Login()
    private val fFeed = Feed()
    private val fProfile = Profile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        //transaction(fFeed)

        //runtime permission check





        //logging and signing check
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)


        // main fragment handler
        main_navigation.setOnNavigationItemSelectedListener {item ->
            when (item.itemId){
                R.id.nav_profile ->{
                    transaction(fLogin)
                    true
                }
                R.id.nav_feed ->{
                    transaction(fFeed)
                    true
                }
                R.id.nav_tag->{
                    true
                }
                else -> false
            }

        }

    }


    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null){
            transaction(fProfile)
        }
        else{
            transaction(fLogin)
        }
    }

    private fun permissionCheck(): Boolean {
        val pInternet: Boolean = checkSelfPermission(android
            .Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
        val pReadExtStorage: Boolean = checkSelfPermission(android
            .Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val pWriteExtStorage: Boolean = checkSelfPermission(android
            .Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (pInternet && pReadExtStorage && pWriteExtStorage) {
            return true
        }
        return false
    }

    private fun toast(status:Int){
        val warning: String = "test toast"
        val tTime : Int = Toast.LENGTH_LONG
        val toast : Toast = Toast.makeText(applicationContext,warning,tTime)
        toast.show()
    }

    private fun transaction(fragment : Fragment){
        val fManager = supportFragmentManager
        val fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.baseFragFrame,fragment)
        fTransaction.commit()
    }
}
